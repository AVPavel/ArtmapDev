import React from "react";
import styles from "./Map.module.css";
import GoogleMapPhoto from "../../assets/images/GoogleMapPh.jpg";

const Map = () => {
  return (
    <div className={styles.mapContainer}>
      <img src={GoogleMapPhoto} alt="amazing" />
    </div>
  );
};

export default Map;
