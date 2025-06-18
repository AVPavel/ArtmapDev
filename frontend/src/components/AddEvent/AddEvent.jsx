import React, { useState, useEffect } from 'react'; // Adaugă useEffect
import { useNavigate } from 'react-router-dom';
import styles from './AddEvent.module.css';
import Logo from "../../assets/images/HomePage/LOGO_artmap.svg";

const AddEvent = () => {
    const navigate = useNavigate();
    // MODIFICAT: Structura stării pentru formular
    const [eventData, setEventData] = useState({
        name: '',
        description: '',
        date: '',
        location: '',
        categoryId: '', // Vom stoca ID-ul categoriei
        genreId: '',    // Vom stoca ID-ul genului
        price: '',
        imageUrl: ''
    });

    // NOU: Stări pentru a stoca datele de la backend
    const [categories, setCategories] = useState([]);
    const [allGenres, setAllGenres] = useState([]);
    const [filteredGenres, setFilteredGenres] = useState([]);

    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    // NOU: Fetch pentru categorii și genuri la încărcarea componentei
    useEffect(() => {
        const fetchInitialData = async () => {
            try {
                // Folosim Promise.all pentru a face cererile în paralel
                const [categoriesResponse, genresResponse] = await Promise.all([
                    fetch('http://localhost:8080/api/categories'),
                    fetch('http://localhost:8080/api/genres')
                ]);

                if (!categoriesResponse.ok || !genresResponse.ok) {
                    throw new Error('Eroare la preluarea datelor inițiale.');
                }

                const categoriesData = await categoriesResponse.json();
                const genresData = await genresResponse.json();

                setCategories(categoriesData);
                setAllGenres(genresData);

            } catch (err) {
                setError('Nu s-au putut încărca categoriile și genurile.');
                console.error(err);
            }
        };

        fetchInitialData();
    }, []); // Se execută o singură dată

    // NOU: Efect pentru a filtra genurile când se schimbă categoria selectată
    useEffect(() => {
        if (eventData.categoryId) {
            const relevantGenres = allGenres.filter(
                (genre) => genre.category && genre.category.id.toString() === eventData.categoryId
            );
            setFilteredGenres(relevantGenres);
            // Resetează genul selectat dacă se schimbă categoria
            setEventData(prev => ({ ...prev, genreId: '' }));
        } else {
            // Dacă nu e selectată nicio categorie, golește lista de genuri
            setFilteredGenres([]);
        }
    }, [eventData.categoryId, allGenres]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEventData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        // ... logica de submit rămâne aproape la fel, doar trimitem ID-urile
        e.preventDefault();
        // ...
        const bodyToSend = {
            ...eventData,
            price: Number(eventData.price), // Asigură-te că prețul este numeric
            date: new Date(eventData.date).toISOString()
        };

        // Trimitem datele către backend
    };

    return (
        <div className={styles.addEventPage}>
            <div className={styles.addEventContainer}>
                {/* Header-ul rămâne la fel */}
                <form onSubmit={handleSubmit} className={styles.eventForm}>
                    <div className={styles.card}>
                        {/* ... câmpurile Nume, Descriere, URL Imagine ... */}

                        <div className={styles.grid}>
                            <div>
                                <label className={styles.label}>Data și Ora</label>
                                <input type="datetime-local" name="date" value={eventData.date} onChange={handleChange} className={styles.input} required />
                            </div>
                            <div>
                                <label className={styles.label}>Locație</label>
                                <input type="text" name="location" value={eventData.location} onChange={handleChange} className={styles.input} required />
                            </div>

                            {/* MODIFICAT: Dropdown dinamic pentru categorii */}
                            <div>
                                <label className={styles.label}>Categorie</label>
                                <select name="categoryId" value={eventData.categoryId} onChange={handleChange} className={styles.input} required>
                                    <option value="">Selectează o categorie</option>
                                    {categories.map(cat => (
                                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                                    ))}
                                </select>
                            </div>

                            {/* NOU: Dropdown dinamic pentru genuri, apare doar dacă există genuri filtrate */}
                            {filteredGenres.length > 0 && (
                                <div>
                                    <label className={styles.label}>Gen</label>
                                    <select name="genreId" value={eventData.genreId} onChange={handleChange} className={styles.input} required>
                                        <option value="">Selectează un gen</option>
                                        {filteredGenres.map(genre => (
                                            <option key={genre.id} value={genre.id}>{genre.name}</option>
                                        ))}
                                    </select>
                                </div>
                            )}

                            <div>
                                <label className={styles.label}>Preț (RON)</label>
                                <input type="number" name="price" value={eventData.price} onChange={handleChange} className={styles.input} placeholder="0 pentru gratuit" required />
                            </div>
                        </div>
                    </div>

                    {error && <p className={styles.errorMessage}>{error}</p>}
                    {success && <p className={styles.successMessage}>{success}</p>}

                    <button type="submit" className={styles.submitButton}>
                        Publică Evenimentul
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AddEvent;