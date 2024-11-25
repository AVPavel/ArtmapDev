import React from "react";
import styles from "./MapSection.module.css";
import MapAddon from "../MapAddon/MapAddon";
import Map from "../Map/Map";

const MapSection = () => {
  return (
    <div className={styles.sectionContainer}>
      <MapAddon />
      <Map />
    </div>
  );
};

export default MapSection;
