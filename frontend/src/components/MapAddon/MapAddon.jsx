import React, { useState, useEffect } from "react";
import styles from "./MapAddon.module.css";

const MapAddon = ({ filters, onFilterChange }) => {
    const [categories, setCategories] = useState([]);
    const [genres, setGenres] = useState([]);

    useEffect(() => {
        // Fetch categories
        fetch("http://localhost:8080/api/categories")
            .then(response => response.json())
            .then(data => setCategories(data))
            .catch(error => console.error("Error fetching categories:", error));

        // Fetch genres
        fetch("http://localhost:8080/api/genres")
            .then(response => response.json())
            .then(data => setGenres(data))
            .catch(error => console.error("Error fetching genres:", error));
    }, []);

    const priceRanges = [
        { value: "0", label: "Toate prețurile" },
        { value: "1", label: "0 - 50 RON" },
        { value: "2", label: "50 - 100 RON" },
        { value: "3", label: "100 - 200 RON" },
        { value: "4", label: "200+ RON" }
    ];

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        onFilterChange({ ...filters, [name]: value });
    };

    const handleDateChange = (e) => {
        const { name, value } = e.target;
        // Convert from dd/mm/yyyy to yyyy-mm-dd for API
        const [day, month, year] = value.split('/');
        const isoDate = year && month && day ? `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}` : '';

        onFilterChange({
            ...filters,
            dateRange: {
                ...filters.dateRange,
                [name]: isoDate
            }
        });
    };

    // Format date from yyyy-mm-dd to dd/mm/yyyy for display
    const formatDateForDisplay = (dateString) => {
        if (!dateString) return '';
        const [year, month, day] = dateString.split('-');
        return `${day}/${month}/${year}`;
    };

    return (
        <div className={styles.mapAddonContainer}>
            <h2>Găsește evenimente aproape de tine</h2>
            <p>Înregistrează-te și vei putea obține</p>
            <p>informații personalizate</p>

            <h3>Filtre:</h3>

            <div className={styles.filtersContainer}>
                <select
                    className={styles.filterDropdown}
                    name="categoryId"
                    value={filters.categoryId}
                    onChange={handleInputChange}
                >
                    <option value="0">Toate categoriile</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.id}>
                            {category.name}
                        </option>
                    ))}
                </select>

                <select
                    className={styles.filterDropdown}
                    name="genreId"
                    value={filters.genreId}
                    onChange={handleInputChange}
                >
                    <option value="0">Toate genurile</option>
                    {genres.map(genre => (
                        <option key={genre.id} value={genre.id}>
                            {genre.name}
                        </option>
                    ))}
                </select>

                <select
                    className={styles.filterDropdown}
                    name="priceRange"
                    value={filters.priceRange}
                    onChange={handleInputChange}
                >
                    {priceRanges.map(range => (
                        <option key={range.value} value={range.value}>
                            {range.label}
                        </option>
                    ))}
                </select>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    <input
                        type="text"
                        className={styles.filterDropdown}
                        name="startDate"
                        placeholder="DD/MM/YYYY"
                        value={formatDateForDisplay(filters.dateRange.start)}
                        onChange={handleDateChange}
                        style={{ minHeight: '40px' }}
                    />
                    <input
                        type="text"
                        className={styles.filterDropdown}
                        name="endDate"
                        placeholder="DD/MM/YYYY"
                        value={formatDateForDisplay(filters.dateRange.end)}
                        onChange={handleDateChange}
                        style={{ minHeight: '40px' }}
                    />
                </div>
            </div>
        </div>
    );
};

export default MapAddon;
