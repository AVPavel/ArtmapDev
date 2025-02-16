import React from "react";
import styles from "./MapAddon.module.css";

const MapAddon = () => {
  return (
      <div className={styles.mapAddonContainer}>
        <h2>Gaseste evenimente aproape de tine</h2>

        <p>Inregistreaza-te si vei putea obtine</p>
        <p>informatii personalizate</p>

        <h3>Filtre:</h3>

        <div className={styles.filtersContainer}>
          <select className={styles.filterDropdown}>
            <option value="0" selected="selected">Categorii</option>
          </select>

          <select className={styles.filterDropdown}>
            <option value="0" selected="selected">Genuri</option>
          </select>

          <select className={styles.filterDropdown}>
            <option value="0" selected="selected">Data</option>
          </select>

          <select className={styles.filterDropdown}>
            <option value="0" selected="selected">Pret</option>
          </select>
        </div>


      </div>
  );
};

export default MapAddon;