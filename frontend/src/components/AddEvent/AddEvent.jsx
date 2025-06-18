import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './AddEvent.module.css';
import Logo from "../../assets/images/HomePage/LOGO_artmap.svg";
import { FaSpinner } from 'react-icons/fa'; // Import spinner icon

const AddEvent = () => {
    const navigate = useNavigate();

    // Starea pentru formular, acum include latitude și longitude
    const [eventData, setEventData] = useState({
        name: '',        // Mapped to 'title' in backend DTO
        description: '',
        date: '',
        location: '',    // Mapped to 'location' in backend DTO (e.g., city/town)
        address: '',     // Mapped to 'address' in backend DTO (e.g., street, number)
        latitude: null,  // Will be set by geocoding
        longitude: null, // Will be set by geocoding
        categoryId: '',
        genreId: '',     // Singular genre ID for the dropdown
        price: '',       // Mapped to 'cheapestTicket' in backend DTO
        imageUrl: ''     // Optional
    });

    const [categories, setCategories] = useState([]);
    const [allGenres, setAllGenres] = useState([]);
    const [filteredGenres, setFilteredGenres] = useState([]);

    const [isLoading, setIsLoading] = useState(false); // General loading for fetches/submit
    const [isGeocoding, setIsGeocoding] = useState(false); // Specific loading for geocoding
    const [currentStep, setCurrentStep] = useState('');

    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const apiKey = process.env.REACT_APP_HERE_API_KEY; // Asigură-te că aceasta este configurată!

    // Fetch pentru categorii și genuri la încărcarea componentei
    useEffect(() => {
        const fetchInitialData = async () => {
            setIsLoading(true);
            setCurrentStep('Se încarcă categoriile și genurile...');
            setError('');

            try {
                const [categoriesResponse, genresResponse] = await Promise.all([
                    fetch('http://localhost:8080/api/categories'),
                    fetch('http://localhost:8080/api/genres')
                ]);

                if (!categoriesResponse.ok) {
                    throw new Error(`Eroare la preluarea categoriilor: ${categoriesResponse.statusText}`);
                }
                if (!genresResponse.ok) {
                    throw new Error(`Eroare la preluarea genurilor: ${genresResponse.statusText}`);
                }

                const categoriesData = await categoriesResponse.json();
                const genresData = await genresResponse.json();

                setCategories(categoriesData);
                setAllGenres(genresData);

            } catch (err) {
                setError(err.message || 'Nu s-au putut încărca datele inițiale (categorii și genuri).');
                console.error("Eroare la fetch-ul inițial:", err);
            } finally {
                setIsLoading(false);
                setCurrentStep('');
            }
        };

        fetchInitialData();
    }, []);

    // Efect pentru a filtra genurile când se schimbă categoria selectată
    useEffect(() => {
        if (eventData.categoryId) {
            const relevantGenres = allGenres.filter(
                (genre) => genre.category && genre.category.id.toString() === eventData.categoryId
            );
            setFilteredGenres(relevantGenres);
            setEventData(prev => ({ ...prev, genreId: '' }));
        } else {
            setFilteredGenres([]);
            setEventData(prev => ({ ...prev, genreId: '' }));
        }
    }, [eventData.categoryId, allGenres]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEventData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    // Funcție de geocodare asincronă
    const geocodeAddress = async (location, address) => {
        const fullAddress = `${address}, ${location}`; // Combinăm locația (orașul) și adresa completă
        if (!apiKey) {
            console.error("HERE API Key nu este configurată!");
            setError("Eroare de configurare: Cheia API pentru geocodare lipsește.");
            return null;
        }
        setIsGeocoding(true);
        setCurrentStep('Se obțin coordonatele...');

        try {
            const response = await fetch(`https://geocode.search.hereapi.com/v1/geocode?q=${encodeURIComponent(fullAddress)}&apikey=${apiKey}`);
            const data = await response.json();

            if (data.items && data.items.length > 0) {
                const position = data.items[0].position;
                setIsGeocoding(false);
                return { latitude: position.lat, longitude: position.lng };
            } else {
                setError('Adresa nu a putut fi geocodată. Te rugăm să verifici adresa și locația.');
                setIsGeocoding(false);
                return null;
            }
        } catch (error) {
            console.error('Eroare la geocodare:', error);
            setError('Eroare la serviciul de geocodare. Te rugăm să încerci din nou mai târziu.');
            setIsGeocoding(false);
            return null;
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setIsLoading(true); // Activează loading pentru întregul proces de submit

        // Extrage token-ul aici, la începutul funcției, pentru a fi disponibil în tot scope-ul
        const token = localStorage.getItem("jwt");
        if (!token) {
            setError("Trebuie să fii autentificat pentru a adăuga un eveniment.");
            setIsLoading(false);
            return;
        }

        // 1. Geocodarea adresei
        setCurrentStep('Se geocodează adresa evenimentului...');
        const coords = await geocodeAddress(eventData.location, eventData.address);

        if (!coords) {
            setIsLoading(false); // Dezactivează loading dacă geocodarea eșuează
            return; // Oprește submit-ul dacă nu avem coordonate
        }

        // Validări înainte de trimitere (la fel ca înainte)
        const formattedDate = new Date(eventData.date).toISOString();
        const priceValue = parseFloat(eventData.price);
        if (isNaN(priceValue)) {
            setError('Prețul trebuie să fie un număr valid.');
            setIsLoading(false);
            return;
        }

        const genreIdsArray = eventData.genreId ? [parseInt(eventData.genreId, 10)] : [];

        // Construiește bodyToSend cu toate câmpurile necesare DTO-ului de backend
        const bodyToSend = {
            title: eventData.name,
            description: eventData.description,
            date: formattedDate,
            location: eventData.location,
            address: eventData.address,
            latitude: coords.latitude, // Folosește coordonatele obținute de la geocodare
            longitude: coords.longitude, // Folosește coordonatele obținute de la geocodare
            categoryId: parseInt(eventData.categoryId, 10),
            genreIds: genreIdsArray,
            cheapestTicket: priceValue,
            ticketPrices: null, // Asumăm null dacă nu avem un input dedicat
            imageUrl: eventData.imageUrl || null // imageUrl este opțional
        };

        // 2. Trimiterea datelor către backend
        setCurrentStep('Se publică evenimentul...');
        try {
            const response = await fetch('http://localhost:8080/api/events/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` // 'token' este acum definit în acest scope
                },
                body: JSON.stringify(bodyToSend)
            });

            if (response.status === 201) {
                setSuccess('Evenimentul a fost adăugat cu succes! Se redirecționează...');
                // Resetăm formularul
                setEventData({ name: '', description: '', date: '', location: '', address: '', latitude: null, longitude: null, categoryId: '', genreId: '', price: '', imageUrl: '' });
                setTimeout(() => navigate('/'), 2000);
            } else {
                const errorData = await response.json();
                setError(errorData.message || 'A apărut o eroare la adăugarea evenimentului. Detalii: ' + (errorData.error || ''));
            }
        } catch (err) {
            setError('Eroare de rețea sau eroare neașteptată. Te rugăm să încerci din nou mai târziu.');
            console.error("Eroare la submit:", err);
        } finally {
            setIsLoading(false); // Dezactivează loading la final
            setCurrentStep('');
        }
    };

    // Starea generală de loading pentru afișarea spinner-ului pe toată pagina
    if (isLoading && categories.length === 0 && allGenres.length === 0) {
        return (
            <div className={styles.addEventPage}>
                <div className={styles.loadingContainer}>
                    <FaSpinner className={styles.spinner} />
                    <p>{currentStep}</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.addEventPage}>
            <div className={styles.addEventContainer}>
                <div className={styles.header}>
                    <div className={styles.logoContainer}>
                        <img src={Logo} alt="Artmap Logo" className={styles.logo} />
                    </div>
                    <h1 className={styles.title}>Adaugă un Eveniment Nou</h1>
                    <p className={styles.description}>Completează detaliile de mai jos pentru a publica un eveniment.</p>
                </div>

                <form onSubmit={handleSubmit} className={styles.eventForm}>
                    <div className={styles.card}>
                        <label className={styles.label}>Nume Eveniment</label>
                        <input type="text" name="name" value={eventData.name} onChange={handleChange} className={styles.input} required disabled={isLoading || isGeocoding} />

                        <label className={styles.label}>Descriere</label>
                        <textarea name="description" value={eventData.description} onChange={handleChange} className={styles.textarea} required disabled={isLoading || isGeocoding}></textarea>

                        <label className={styles.label}>URL Imagine (opțional)</label>
                        <input type="text" name="imageUrl" value={eventData.imageUrl} onChange={handleChange} className={styles.input} placeholder="https://exemplu.com/imagine.jpg" disabled={isLoading || isGeocoding} />

                        <div className={styles.grid}>
                            <div>
                                <label className={styles.label}>Data și Ora</label>
                                <input type="datetime-local" name="date" value={eventData.date} onChange={handleChange} className={styles.input} required disabled={isLoading || isGeocoding} />
                            </div>
                            <div>
                                <label className={styles.label}>Locație (Oraș/Localitate)</label>
                                <input type="text" name="location" value={eventData.location} onChange={handleChange} className={styles.input} required disabled={isLoading || isGeocoding} />
                            </div>
                            <div>
                                <label className={styles.label}>Adresă completă (Strada, Nr., Bloc, etc.)</label>
                                <input type="text" name="address" value={eventData.address} onChange={handleChange} className={styles.input} required disabled={isLoading || isGeocoding} />
                            </div>
                            <div>
                                <label className={styles.label}>Categorie</label>
                                <select name="categoryId" value={eventData.categoryId} onChange={handleChange} className={styles.input} required disabled={isLoading || isGeocoding}>
                                    <option value="">Selectează o categorie</option>
                                    {categories.map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.display_name || cat.name}</option>
                                    ))}
                                </select>
                            </div>

                            {eventData.categoryId && (
                                <div>
                                    <label className={styles.label}>Gen (opțional)</label>
                                    <select name="genreId" value={eventData.genreId} onChange={handleChange} className={styles.input} disabled={isLoading || isGeocoding}>
                                        <option value="">Selectează un gen</option>
                                        {filteredGenres.map(genre => (
                                            <option key={genre.id} value={genre.id}>{genre.name}</option>
                                        ))}
                                    </select>
                                </div>
                            )}

                            <div>
                                <label className={styles.label}>Preț (RON)</label>
                                <input type="number" name="price" value={eventData.price} onChange={handleChange} className={styles.input} placeholder="0 pentru gratuit" required disabled={isLoading || isGeocoding} />
                            </div>
                        </div>
                    </div>

                    {error && <p className={styles.errorMessage}>{error}</p>}
                    {success && <p className={styles.successMessage}>{success}</p>}

                    <button type="submit" className={styles.submitButton} disabled={isLoading || isGeocoding}>
                        {isLoading || isGeocoding ? (
                            <div className={styles.loadingContainerButton}>
                                <FaSpinner className={styles.spinner} />
                                {currentStep}
                            </div>
                        ) : 'Publică Evenimentul'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AddEvent;
