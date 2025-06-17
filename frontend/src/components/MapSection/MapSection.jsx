import React, { useState } from "react";
import styles from "./MapSection.module.css";
import MapAddon from "../MapAddon/MapAddon";
import Map from "../Map/Map";

const MapSection = () => {
    const [filters, setFilters] = useState({
        categoryId: "0",
        genreId: "0",
        priceRange: "0",
        dateRange: {
            start: "",
            end: ""
        },
        searchTerm: ""
    });

    return (
        <div className={styles.sectionContainer}>
            <MapAddon filters={filters} onFilterChange={setFilters} />
            <Map filters={filters} />
        </div>
    );
};

export default MapSection;
