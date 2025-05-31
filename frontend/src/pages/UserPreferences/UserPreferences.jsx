import React, { useState } from 'react';
import styles from './UserPreferences.module.css';

const UserPreferences = () => {
    const [preferences, setPreferences] = useState({
        concerte: false,
        concertGenres: [],
        teatru: false,
        teatruGenres: [],
        festivaluri: false,
        festivalGenres: [],
        alteEvenimente: []
    });

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

    const handleToggle = (field) => {
        setPreferences(prev => ({
            ...prev,
            [field]: !prev[field],
            [`${field}Genres`]: []
        }));
    };

    const handleGenreChange = (genre, field) => {
        setPreferences(prev => {
            const genres = prev[field].includes(genre)
                ? prev[field].filter(g => g !== genre)
                : [...prev[field], genre];
            return { ...prev, [field]: genres };
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log(preferences);
    };

    return (
        <div className={styles.preferencesPage}>
            <div className={styles.preferencesContainer}>
                <div className={styles.header}>
                    <div className={styles.logoPlaceholder}></div>
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
                                >
                                    Da
                                </button>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${!preferences.concerte ? styles.active : ''}`}
                                    onClick={() => handleToggle('concerte')}
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
                                >
                                    Da
                                </button>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${!preferences.teatru ? styles.active : ''}`}
                                    onClick={() => handleToggle('teatru')}
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
                                >
                                    Da
                                </button>
                                <button
                                    type="button"
                                    className={`${styles.toggleButton} ${!preferences.festivaluri ? styles.active : ''}`}
                                    onClick={() => handleToggle('festivaluri')}
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
                                        />
                                        <span className={styles.genreName}>{event}</span>
                                    </label>
                                ))}
                            </div>
                        </div>
                    </div>

                    <button type="submit" className={styles.submitButton}>
                        Salvează Preferințele
                    </button>
                </form>
            </div>
        </div>
    );
};

export default UserPreferences;
