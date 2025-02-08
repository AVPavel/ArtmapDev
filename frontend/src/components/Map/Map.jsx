import React, { useEffect, useRef, useState } from 'react';
import styles from './Map.module.css';

const Map = () => {
    const mapRef = useRef(null);
    const [map, setMap] = useState(null);
    const apiKey = process.env.REACT_APP_HERE_API_KEY;

    useEffect(() => {
        if (!window.H || !mapRef.current) return;

        const platform = new window.H.service.Platform({
            apikey: apiKey,
        });

        const defaultLayers = platform.createDefaultLayers();

        const hMap = new window.H.Map(mapRef.current, defaultLayers.vector.normal.map, {
            center: { lat: 44.4361414, lng: 26.1027202 },
            zoom: 17,
            pixelRatio: window.devicePixelRatio || 1,
        });

        const behavior = new window.H.mapevents.Behavior(new window.H.mapevents.MapEvents(hMap));

        const ui = window.H.ui.UI.createDefault(hMap, defaultLayers);

        setMap(hMap);

        return () => {
            hMap.dispose();
        };
    }, [apiKey]);

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            const query = event.target.value;
            if (query) {
                geocodeAddress(query);
            }
        }
    };

    const geocodeAddress = (address) => {
        fetch(`https://geocode.search.hereapi.com/v1/geocode?q=${encodeURIComponent(address)}&apikey=${apiKey}`)
            .then((response) => response.json())
            .then((data) => {
                if (data.items && data.items.length > 0) {
                    const position = data.items[0].position;
                    const coordinates = { lat: position.lat, lng: position.lng };
                    centerMapOnCoordinates(coordinates);
                } else {
                    console.error('No results found');
                }
            })
            .catch((error) => {
                console.error('Geocoding error:', error);
            });
    };

    const centerMapOnCoordinates = (coords) => {
        if (map) {
            map.setCenter(coords);
            map.setZoom(16
            );
            const marker = new window.H.map.Marker(coords);
            map.addObject(marker);
        }
    };

    return (
        <div className={styles.mapContainer}>
            <input
                type="text"
                placeholder="Search for address..."
                className={styles.searchInput}
                onKeyDown={handleKeyDown}
            />
            <div ref={mapRef}
                 className={styles.map}
                 style={{ height: '500px', width: '100%' }}
            />
        </div>
    );
};

export default Map;
