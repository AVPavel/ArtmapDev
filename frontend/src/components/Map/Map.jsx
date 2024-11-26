import React from "react";
import styles from "./Map.module.css";
import GoogleMapPhoto from "../../assets/images/GoogleMaps.png";

const Map = () => {
    return (
        <div className={styles.mapContainer}>
            <div className={styles.overMapAndLabel}>
                <p>Map</p>
                <div className={styles.overMap}>
                    <input type="text" maxLength="70" placeholder="Search for address..."/>
                    <img src={GoogleMapPhoto} alt="amazing"/>
                </div>
            </div>

        </div>
    );
};

export default Map;
