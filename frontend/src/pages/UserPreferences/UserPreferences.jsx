import React, { useState, useEffect } from 'react';
import styles from './UserPreferences.module.css';
import Logo from "../../assets/images/HomePage/LOGO_artmap.svg";
import { useNavigate } from 'react-router-dom'; // Import useNavigate
import { FaSpinner } from 'react-icons/fa'; // Import spinner icon

const UserPreferences = () => {
    const [preferences, setPreferences] = useState({
        concerte: false,
        concertGenres: [],
        teatru: false,
        teatruGenres: [],
        festivaluri: false,
        festivalGenres: [],
        alteEvenimente: [] // This will store names of other events
    });

    const [categories, setCategories] = useState([]);
    const [genres, setGenres] = useState([]);
    const [dataLoading, setDataLoading] = useState(true); // Pentru încărcarea inițială a datelor
    const [dataError, setDataError] = useState(''); // Pentru erorile la încărcarea inițială

    const [isLoading, setIsLoading] = useState(false); // Pentru trimiterea formularului
    const [currentStep, setCurrentStep] = useState(''); // Pentru pașii de trimitere a formularului
    const [error, setError] = useState(''); // Pentru erorile la trimiterea formularului
    const [success, setSuccess] = useState(''); // Pentru succesul trimiterii formularului

    const navigate = useNavigate();

    // Numele categoriilor și genurilor afișate în frontend.
    // Acestea vor fi mapate la ID-urile din backend după încărcarea datelor.
    const musicGenres = ['Rock', 'Pop', 'Jazz', 'Electro', 'Hip-Hop', 'Manele', 'Metal', 'Muzică clasică', 'Latino', 'Folk', 'Reggaeton', 'Muzică populară'];
    const theaterGenres = ['Dramă', 'Comedie', 'Musical', 'Monodramă', 'Satiră', 'Experimental'];
    const festivalTypes = ['Muzică', 'Film', 'Artă vizuală', 'Cultură', 'Gastronomie', 'Artă stradală'];
    const otherEvents = [
        'Workshop-uri',
        'Conferințe',
        'Evenimente de familie',
        'Târguri și expoziții',
        'Sport și activități fizice',
        'Evenimente culinare',
        'Proiecții de filme',
        'Evenimente culturale'
    ];

    // Încarcă categoriile și genurile de la backend la montarea componentei
    useEffect(() => {
        const fetchMetadata = async () => {
            setCurrentStep('Se încarcă categoriile și genurile...');
            setDataLoading(true);
            setDataError('');
            try {
                const categoriesResponse = await fetch('http://localhost:8080/api/categories');
                const genresResponse = await fetch('http://localhost:8080/api/genres');

                if (!categoriesResponse.ok) {
                    throw new Error('Nu s-au putut încărca categoriile.');
                }
                if (!genresResponse.ok) {
                    throw new Error('Nu s-au putut încărca genurile.');
                }

                const categoriesData = await categoriesResponse.json();
                const genresData = await genresResponse.json();

                setCategories(categoriesData);
                setGenres(genresData);
            } catch (err) {
                setDataError(err.message || 'Nu s-au putut încărca datele necesare.');
            } finally {
                setDataLoading(false);
                setCurrentStep('');
            }
        };
        fetchMetadata();
    }, []);

    const handleToggle = (field) => {
        setPreferences(prev => ({
            ...prev,
            [field]: !prev[field],
            [`${field}Genres`]: [] // Golește genurile atunci când se selectează "Nu"
        }));
    };

    const handleGenreChange = (genre, field) => {
        setPreferences(prev => {
            const genresArray = prev[field].includes(genre)
                ? prev[field].filter(g => g !== genre)
                : [...prev[field], genre];
            return { ...prev, [field]: genresArray };
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setIsLoading(true);
        setCurrentStep('Se pregătesc preferințele...');

        const jwtToken = localStorage.getItem('jwt');
        if (!jwtToken) {
            setError('Token de autentificare negăsit. Vă rugăm să vă autentificați.');
            setIsLoading(false);
            return;
        }

        try {
            // Funcții ajutătoare pentru a găsi ID-uri din datele încărcate
            const findCategoryId = (name) => categories.find(cat => cat.name === name)?.id;
            const findGenreId = (name) => genres.find(g => g.name === name)?.id;

            let preferencesToSend = [];

            // Obține ID-ul genului generic "Niciuna"
            const niciunaGenreId = findGenreId('Niciuna');
            if (!niciunaGenreId) {
                console.error("Genul 'Niciuna' nu a fost găsit în datele de backend.");
                setError("O eroare a apărut la pregătirea preferințelor. Genul 'Niciuna' lipsește.");
                setIsLoading(false);
                return;
            }

            // Pentru Concerte
            const concertCategoryId = findCategoryId('Concert'); // Using 'Concert' based on category display_name in SQL
            if (concertCategoryId) {
                if (preferences.concerte) {
                    // Utilizatorul a selectat 'Da' pentru concerte, adaugă genuri specifice
                    preferences.concertGenres.forEach(genreName => {
                        const genreId = findGenreId(genreName);
                        if (genreId) {
                            preferencesToSend.push({ categoryId: concertCategoryId, genreId: genreId });
                        } else {
                            console.warn(`ID-ul genului nu a fost găsit pentru genul de concert: ${genreName}`);
                        }
                    });
                } else {
                    // Utilizatorul a selectat 'Nu' pentru concerte, adaugă preferința generică 'Niciuna' pentru această categorie
                    preferencesToSend.push({ categoryId: concertCategoryId, genreId: niciunaGenreId });
                }
            } else {
                console.warn("Categoria 'Concert' nu a fost găsită în datele de backend.");
            }


            const teatruCategoryId = findCategoryId('Teatru');
            if (teatruCategoryId) {
                if (preferences.teatru) {
                    preferences.teatruGenres.forEach(genreName => {
                        const genreId = findGenreId(genreName);
                        if (genreId) {
                            preferencesToSend.push({ categoryId: teatruCategoryId, genreId: genreId });
                        } else {
                            console.warn(`ID-ul genului nu a fost găsit pentru genul de teatru: ${genreName}`);
                        }
                    });
                } else {
                    preferencesToSend.push({ categoryId: teatruCategoryId, genreId: niciunaGenreId });
                }
            } else {
                console.warn("Categoria 'Teatru' nu a fost găsită în datele de backend.");
            }


            const festivaluriCategoryId = findCategoryId('Festival');
            if (festivaluriCategoryId) {
                if (preferences.festivaluri) {
                    preferences.festivalGenres.forEach(genreName => {
                        const genreId = findGenreId(genreName);
                        if (genreId) {
                            preferencesToSend.push({ categoryId: festivaluriCategoryId, genreId: genreId });
                        } else {
                            console.warn(`ID-ul genului nu a fost găsit pentru tipul de festival: ${genreName}`);
                        }
                    });
                } else {
                    preferencesToSend.push({ categoryId: festivaluriCategoryId, genreId: niciunaGenreId });
                }
            } else {
                console.warn("Categoria 'Festival' nu a fost găsită în datele de backend.");
            }


            const alteEvenimenteCategoryId = findCategoryId('Alte Evenimente');
            if (alteEvenimenteCategoryId) {
                preferences.alteEvenimente.forEach(eventName => {
                    const eventGenreId = findGenreId(eventName);
                    if (eventGenreId) {
                        preferencesToSend.push({ categoryId: alteEvenimenteCategoryId, genreId: eventGenreId });
                    } else {
                        console.warn(`ID-ul genului nu a fost găsit pentru evenimentul: ${eventName}`);
                    }
                });
            } else {
                console.warn("Categoria 'Alte Evenimente' nu a fost găsită în datele de backend.");
            }

            if (preferencesToSend.length === 0) {
                setError('Nu au fost selectate preferințe de salvat.');
                setIsLoading(false);
                return;
            }

            setCurrentStep('Se trimit preferințele...');
            for (let i = 0; i < preferencesToSend.length; i++) {
                const pref = preferencesToSend[i];
                setCurrentStep(`Se salvează preferința ${i + 1} din ${preferencesToSend.length}...`);
                const response = await fetch('http://localhost:8080/api/preferences/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${jwtToken}`
                    },
                    body: JSON.stringify(pref),
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'A eșuat salvarea unor preferințe.');
                }
            }

            setSuccess('Preferințe salvate cu succes! Se redirecționează către pagina principală...');
            setTimeout(() => navigate('/'), 1500);
        } catch (err) {
            setError(err.message || 'Eroare la salvarea preferințelor. Vă rugăm să încercați din nou.');
        } finally {
            setIsLoading(false);
        }
    };

    if (dataLoading) {
        return (
            <div className={styles.preferencesPage}>
                <div className={styles.loadingContainer}>
                    <FaSpinner className={styles.spinner} />
                    <p>{currentStep || "Se încarcă datele inițiale..."}</p>
                </div>
            </div>
        );
    }

    if (dataError) {
        return (
            <div className={styles.preferencesPage}>
                <div className={styles.error}>
                    <p>Eroare: {dataError}</p>
                    <p>Vă rugăm să vă asigurați că serviciile backend rulează și sunt accesibile.</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.preferencesPage}>
            <div className={styles.preferencesContainer}>
                <div className={styles.header}>
                    <div className={styles.logoContainer}>
                        <img src={Logo} alt="Logo Artmap" className={styles.logo} />
                    </div>
                    <h1 className={styles.preferencesTitle}>Preferințe Evenimente</h1>
                    <p className={styles.description}>
                        Selectează tipurile de evenimente care te interesează pentru a primi recomandări personalizate.
                    </p>
                </div>

                <form onSubmit={handleSubmit} className={styles.preferencesForm}>
                    <div className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Concerte</h2>
                            <div className={styles.toggleContainer}>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${preferences.concerte ? styles.active : ''}`}
                                    onClick={() => handleToggle('concerte')}
                                    disabled={isLoading}
                                >
                                    Da
                                </button>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${!preferences.concerte ? styles.active : ''}`}
                                    onClick={() => handleToggle('concerte')}
                                    disabled={isLoading}
                                >
                                    Nu
                                </button>
                            </div>
                        </div>

                        {preferences.concerte && (
                            <div className={styles.genreSelection}>
                                <p>Selectează genurile muzicale preferate:</p>
                                <div className={styles.genreGrid}>
                                    {musicGenres.map(genre => (
                                        <label key={genre} className={styles.genreLabel}>
                                            <input
                                                type="checkbox"
                                                checked={preferences.concertGenres.includes(genre)}
                                                onChange={() => handleGenreChange(genre, 'concertGenres')}
                                                disabled={isLoading}
                                            />
                                            <span className={styles.genreName}>{genre}</span>
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    <div className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Teatru</h2>
                            <div className={styles.toggleContainer}>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${preferences.teatru ? styles.active : ''}`}
                                    onClick={() => handleToggle('teatru')}
                                    disabled={isLoading}
                                >
                                    Da
                                </button>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${!preferences.teatru ? styles.active : ''}`}
                                    onClick={() => handleToggle('teatru')}
                                    disabled={isLoading}
                                >
                                    Nu
                                </button>
                            </div>
                        </div>

                        {preferences.teatru && (
                            <div className={styles.genreSelection}>
                                <p>Selectează genurile de teatru preferate:</p>
                                <div className={styles.genreGrid}>
                                    {theaterGenres.map(genre => (
                                        <label key={genre} className={styles.genreLabel}>
                                            <input
                                                type="checkbox"
                                                checked={preferences.teatruGenres.includes(genre)}
                                                onChange={() => handleGenreChange(genre, 'teatruGenres')}
                                                disabled={isLoading}
                                            />
                                            <span className={styles.genreName}>{genre}</span>
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    <div className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Festivaluri</h2>
                            <div className={styles.toggleContainer}>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${preferences.festivaluri ? styles.active : ''}`}
                                    onClick={() => handleToggle('festivaluri')}
                                    disabled={isLoading}
                                >
                                    Da
                                </button>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${!preferences.festivaluri ? styles.active : ''}`}
                                    onClick={() => handleToggle('festivaluri')}
                                    disabled={isLoading}
                                >
                                    Nu
                                </button>
                            </div>
                        </div>

                        {preferences.festivaluri && (
                            <div className={styles.genreSelection}>
                                <p>Selectează tipurile de festivaluri preferate:</p>
                                <div className={styles.genreGrid}>
                                    {festivalTypes.map(genre => (
                                        <label key={genre} className={styles.genreLabel}>
                                            <input
                                                type="checkbox"
                                                checked={preferences.festivalGenres.includes(genre)}
                                                onChange={() => handleGenreChange(genre, 'festivalGenres')}
                                                disabled={isLoading}
                                            />
                                            <span className={styles.genreName}>{genre}</span>
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    <div className={styles.card}>
                        <div className={styles.cardHeader}>
                            <h2>Alte Evenimente</h2>
                        </div>
                        <div className={styles.genreSelection}>
                            <p>Selectează alte tipuri de evenimente care te interesează:</p>
                            <div className={styles.genreGrid}>
                                {otherEvents.map(event => (
                                    <label key={event} className={styles.genreLabel}>
                                        <input
                                            type="checkbox"
                                            checked={preferences.alteEvenimente.includes(event)}
                                            onChange={() => handleGenreChange(event, 'alteEvenimente')}
                                            disabled={isLoading}
                                        />
                                        <span className={styles.genreName}>{event}</span>
                                    </label>
                                ))}
                            </div>
                        </div>
                    </div>

                    <button
                        type="submit"
                        className={styles.submitButton}
                        disabled={isLoading || dataLoading}
                    >
                        {isLoading ? (
                            <div className={styles.loadingContainer}>
                                <FaSpinner className={styles.spinner} />
                                {currentStep}
                            </div>
                        ) : 'Salvează Preferințele'}
                    </button>
                </form>

                {isLoading && !dataLoading && (
                    <div className={styles.progressContainer}>
                        <div className={styles.progressText}>{currentStep}</div>
                    </div>
                )}

                {error && <div className={styles.error}>{error}</div>}
                {success && <div className={styles.success}>{success}</div>}
            </div>
        </div>
    );
};

export default UserPreferences;
