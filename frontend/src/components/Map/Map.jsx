import React, { useEffect, useRef, useState } from 'react';
import styles from './Map.module.css';

const Map = () => {
    const mapRef = useRef(null);
    const [map, setMap] = useState(null);
    const [ui, setUi] = useState(null);
    const [userLocation, setUserLocation] = useState(null);
    const [mapViewType, setMapViewType] = useState('vector.normal.map');
    const apiKey = process.env.REACT_APP_HERE_API_KEY;

    //initializing the map
    useEffect(() => {
        if (!window.H || !mapRef.current) return;

        const platform = new window.H.service.Platform({
            apikey: apiKey,
        });

        // Create only the normal vector layer
        const normalLayer = platform.createDefaultLayers().vector.normal.map;

        const hMap = new window.H.Map(mapRef.current, normalLayer, {
            center: { lat: 44.4361414, lng: 26.1027202 },
            zoom: 17,
            pixelRatio: window.devicePixelRatio || 1,
        });

        const behavior = new window.H.mapevents.Behavior(new window.H.mapevents.MapEvents(hMap));

        const mapUI = window.H.ui.UI.createDefault(hMap, normalLayer);

        setUi(mapUI);
        setMap(hMap);

        return () => {
            hMap.dispose();
        };
    }, [apiKey, mapViewType]);

    //Hook for pointing the current location
    useEffect(() => {
        if (!map) return;

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    setUserLocation({ lat: latitude, lng: longitude });

                    // Create interactive marker
                    const userMarker = createInteractiveMarker(
                        { lat: latitude, lng: longitude },
                        `
            <div style="width: 450px; height: 225px; border: 1px solid #ccc; padding: 10px; overflow: hidden;">
  <h3>Your Location</h3>
  <p>Lat: ${latitude.toFixed(4)}</p>
  <p>Lng: ${longitude.toFixed(4)}</p>
  <a href="/details" target="_blank">View Details</a>
</div>

          `,
                        map,
                        ui
                    );
                    map.addObject(userMarker);
                    map.setCenter({ lat: latitude, lng: longitude });
                },
                (error) => {
                    console.error('Error getting user location:', error);
                }
            );
        } else {
            console.log("Geolocation is not supported");
        }
    }, [map]);

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            const query = event.target.value;
            if (query) {
                geocodeAddress(query);
            }
        }
    };
    const createInteractiveMarker = (coords, content, map, ui) => {
        const marker = new window.H.map.Marker(coords);

        // Add click/tap event listener
        marker.addEventListener('tap', (evt) => {
            // Clear previous bubbles
            ui.getBubbles().forEach(bubble => ui.removeBubble(bubble));

            // Create info bubble
            const bubble = new window.H.ui.InfoBubble(evt.target.getGeometry(), {
                content: `<div class="info-window">${content}</div>`,
            });

            // Add bubble to UI
            ui.addBubble(bubble);
        });

        return marker;
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
        if (map && ui) {
            map.setCenter(coords);
            map.setZoom(16);

            // Create interactive marker
            const marker = createInteractiveMarker(
                coords,
                `
        <h3>Searched Location</h3>
        <p>Lat: ${coords.lat.toFixed(4)}</p>
        <p>Lng: ${coords.lng.toFixed(4)}</p>
        <a href="/details?lat=${coords.lat}&lng=${coords.lng}" target="_blank">View Details</a>
      `,
                map,
                ui
            );

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
