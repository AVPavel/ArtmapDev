import React, { useState } from 'react';
import styles from './UserPreferences.module.css';
import GoogleLogo from '../../assets/images/icons/Google_logo.png';

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

    const musicGenres = ['Rock', 'Pop', 'Jazz', 'Electro', 'Hip-Hop', 'Manele', 'Metal', 'Muzică clasică', 'Latino', 'Folk', 'Reggaeton', 'Muzica populară'];
    const theaterGenres = ['Dramă', 'Comedie', 'Musical', 'Monodramă', 'Satiră', 'Experimental'];
    const festivalTypes = ['Muzică', 'Film', 'Artă vizuală', 'Cultură', 'Gastronomie', 'Artă stradala'];
    const otherEvents = [
        'Workshop-uri (Design, programare, fotografie, etc.)',
        'Conferințe',
        'Evenimente de familie',
        'Târguri și expoziții',
        'Sport și Activități fizice',
        'Evenimente culinare',
        'Proiecții de filme',
        'Evenimente culturale și tradiționale'
    ];

    const handleToggle = (field) => {
        setPreferences(prev => ({
            ...prev,
            [field]: !prev[field],
            [`${field}Genres`]: [] // Reset genres when toggling
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
        // Handle form submission here
        console.log(preferences);
    };

    return (
        <div className={styles.preferencesPage}>
            <div className={styles.imagePlaceholder}></div>

            <div className={styles.preferencesContainer}>
                <h1 className={styles.preferencesTitle}>Preferințe Evenimente</h1>
                <p className={styles.description}>
                    Selectează tipurile de evenimente care te interesează:
                </p>

                <form onSubmit={handleSubmit}>
                    {/* Concerts Section */}
                    <div className={styles.section}>
                        <div className={styles.toggleGroup}>
                            <label>Te atrag concertele?</label>
                            <div className={styles.toggleButtons}>
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
                            <div className={styles.genreGroup}>
                                <label>Genuri muzicale preferate:</label>
                                <div className={styles.checkboxGrid}>
                                    {musicGenres.map(genre => (
                                        <label key={genre} className={styles.checkboxLabel}>
                                            <input
                                                type="checkbox"
                                                checked={preferences.concertGenres.includes(genre)}
                                                onChange={() => handleGenreChange(genre, 'concertGenres')}
                                            />
                                            {genre}
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Theater Section */}
                    <div className={styles.section}>
                        <div className={styles.toggleGroup}>
                            <label>Îți place teatrul?</label>
                            <div className={styles.toggleButtons}>
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
                            <div className={styles.genreGroup}>
                                <label>Genuri de teatru preferate:</label>
                                <div className={styles.checkboxGrid}>
                                    {theaterGenres.map(genre => (
                                        <label key={genre} className={styles.checkboxLabel}>
                                            <input
                                                type="checkbox"
                                                checked={preferences.teatruGenres.includes(genre)}
                                                onChange={() => handleGenreChange(genre, 'teatruGenres')}
                                            />
                                            {genre}
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Festivals Section */}
                    <div className={styles.section}>
                        <div className={styles.toggleGroup}>
                            <label>Îți plac festivalurile?</label>
                            <div className={styles.toggleButtons}>
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
                            <div className={styles.genreGroup}>
                                <label>Tipuri de festivaluri preferate:</label>
                                <div className={styles.checkboxGrid}>
                                    {festivalTypes.map(genre => (
                                        <label key={genre} className={styles.checkboxLabel}>
                                            <input
                                                type="checkbox"
                                                checked={preferences.festivalGenres.includes(genre)}
                                                onChange={() => handleGenreChange(genre, 'festivalGenres')}
                                            />
                                            {genre}
                                        </label>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Other Events */}
                    <div className={styles.section}>
                        <div className={styles.genreGroup}>
                            <label>Alte evenimente preferate:</label>
                            <div className={styles.checkboxGrid}>
                                {otherEvents.map(event => (
                                    <label key={event} className={styles.checkboxLabel}>
                                        <input
                                            type="checkbox"
                                            checked={preferences.alteEvenimente.includes(event)}
                                            onChange={() => handleGenreChange(event, 'alteEvenimente')}
                                        />
                                        {event}
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