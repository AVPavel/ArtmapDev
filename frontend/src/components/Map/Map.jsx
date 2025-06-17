import React, { useEffect, useRef, useState } from 'react';
import styles from './Map.module.css';

// Importăm iconițele
import concertIcon from '../../assets/images/icons/iconita_muzica.png';
import theaterIcon from '../../assets/images/icons/iconita_teatru.png';
import exhibitionIcon from '../../assets/images/icons/iconita_arta.png';
import festivalIcon from '../../assets/images/icons/iconita_muzica.png';
import workshopIcon from '../../assets/images/icons/iconita_carte.png';
import defaultIcon from '../../assets/images/icons/iconita_Lupa.png';

const Map = ({ filters }) => {
    const mapRef = useRef(null);
    const [map, setMap] = useState(null);
    const [ui, setUi] = useState(null);
    const [userLocation, setUserLocation] = useState(null);
    const [mapViewType, setMapViewType] = useState('vector.normal.map');
    const [initialLoad, setInitialLoad] = useState(true);
    const apiKey = process.env.REACT_APP_HERE_API_KEY;

    const categoryIcons = {
        '1': concertIcon,
        '2': theaterIcon,
        '3': exhibitionIcon,
        '4': festivalIcon,
        '5': workshopIcon,
    };

    useEffect(() => {
        if (!window.H || !mapRef.current) return;

        const platform = new window.H.service.Platform({
            apikey: apiKey,
        });

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

    useEffect(() => {
        if (!map) return;

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    setUserLocation({ lat: latitude, lng: longitude });

                    const userMarker = createInteractiveMarker(
                        { lat: latitude, lng: longitude },
                        `
                            <div style="width: 450px; height: 225px; border: 1px solid #ccc; padding: 10px; overflow: hidden;">
                                <h3>Your Location</h3>
                                <p>Lat: ${latitude.toFixed(4)}</p>
                                <p>Lng: ${longitude.toFixed(4)}</p>
                            </div>
                        `,
                        map,
                        ui,
                        defaultIcon
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
    }, [map, ui]);


    const createInteractiveMarker = (coords, content, map, ui, iconUrl) => {
        // debugger;
        //console.log('Creating marker with icon:', iconUrl);
        const icon = new window.H.map.Icon(iconUrl, {
            size: { w: 32, h: 32 },
            anchor: { x: 16, y: 32 }
        });

        const marker = new window.H.map.Marker(coords, { icon: icon });

        marker.addEventListener('tap', (evt) => {
            ui.getBubbles().forEach(bubble => ui.removeBubble(bubble));
            const bubble = new window.H.ui.InfoBubble(evt.target.getGeometry(), {
                content: `<div class="info-window">${content}</div>`,
            });
            ui.addBubble(bubble);
        });

        return marker;
    };

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
        if (map && ui) {
            map.setCenter(coords);
            map.setZoom(16);
            const marker = createInteractiveMarker(
                coords,
                `
                    <h3>Searched Location</h3>
                    <p>Lat: ${coords.lat.toFixed(4)}</p>
                    <p>Lng: ${coords.lng.toFixed(4)}</p>
                `,
                map,
                ui,
                defaultIcon
            );
            map.addObject(marker);
        }
    };


    const fetchAndDisplayEvents = async (params) => {
        if (!map || !ui) return;

        try {

            const objects = map.getObjects();
            const markersToRemove = objects.filter(obj =>
                obj instanceof window.H.map.Marker &&
                !(obj.getGeometry().lat === userLocation?.lat && obj.getGeometry().lng === userLocation?.lng)
            );
            map.removeObjects(markersToRemove);

            console.log('Fetching events with params:', params);

            const response = await fetch(`http://localhost:8080/api/events/search?${params}`);

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }

            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                const text = await response.text();
                throw new Error(`Expected JSON but got: ${text}`);
            }

            const data = await response.json();
            console.log('Received events data:', data);

            if (data.content && data.content.length > 0) {
                // debugger;
                data.content.forEach(event => {

                    if (event.latitude && event.longitude) {
                        const coords = {
                            lat: parseFloat(event.latitude),
                            lng: parseFloat(event.longitude)
                        };
                        console.log(event.categoryId);
                        const categoryId = event.categoryId ? String(event.categoryId) : '0';
                        console.log('Event category ID:', categoryId, typeof categoryId);
                        console.log('Available icon keys:', Object.keys(categoryIcons));

                        const iconUrl = categoryIcons[categoryId] || defaultIcon;
                        //console.log('Selected icon:', iconUrl);


                        const content = `
                            <div class="${styles.infoWindow}">
                                <h3>${event.title || 'No title'}</h3>
                                <p>${event.description || 'No description'}</p>
                                <p><strong>Location:</strong> ${event.location || 'Unknown'}</p>
                                <p><strong>Date:</strong> ${event.date ? new Date(event.date).toLocaleDateString() : 'Unknown'}</p>
                                <p><strong>Price:</strong> ${event.price ? `${event.price} RON` : 'Free'}</p>
                                <a href="/events/${event.id}" target="_blank">View Details</a>
                            </div>
                        `;

                        const marker = createInteractiveMarker(coords, content, map, ui, iconUrl);
                        map.addObject(marker);
                    }
                });

                if (initialLoad && data.content.length > 0) {
                    const firstEvent = data.content[0];
                    if (firstEvent.latitude && firstEvent.longitude) {
                        map.setCenter({
                            lat: parseFloat(firstEvent.latitude),
                            lng: parseFloat(firstEvent.longitude)
                        });
                        setInitialLoad(false);
                    }
                }
            } else {
                console.log('No events found with the current filters');
            }
        } catch (error) {
            console.error('Error fetching events:', error);
        }
    };

    useEffect(() => {
        if (map && ui && initialLoad) {
            const params = new URLSearchParams();
            params.append('searchTerm', '');
            params.append('page', '0');
            params.append('size', '50');
            fetchAndDisplayEvents(params.toString());
        }
    }, [map, ui, initialLoad]);

    useEffect(() => {
        if (!map || !ui) return;

        const params = new URLSearchParams();
        params.append('searchTerm', filters.searchTerm || '');
        params.append('page', '0');
        params.append('size', '50');

        if (filters.categoryId && filters.categoryId !== '0') {
            params.append('category', filters.categoryId);
        }

        if (filters.genreId && filters.genreId !== '0') {
            params.append('genre', filters.genreId);
        }

        if (filters.priceRange && filters.priceRange !== '0') {
            params.append('priceRange', filters.priceRange);
        }

        if (filters.dateRange.start) {
            params.append('startDate', filters.dateRange.start);
        }

        if (filters.dateRange.end) {
            params.append('endDate', filters.dateRange.end);
        }

        console.log('Applying filters:', Object.fromEntries(params.entries()));
        fetchAndDisplayEvents(params.toString());
    }, [map, ui, filters]);

    return (
        <div className={styles.mapContainer}>
            <input
                type="text"
                placeholder="Search for address..."
                className={styles.searchInput}
                onKeyDown={handleKeyDown}
            />
            <div
                ref={mapRef}
                className={styles.map}
                style={{ height: '500px', width: '100%' }}
            />

            <div style={{ display: 'none' }}>
                <img src={concertIcon} alt="test concert" />
                <img src={theaterIcon} alt="test theater" />
                <img src={exhibitionIcon} alt="test exhibition" />
                <img src={festivalIcon} alt="test festival" />
                <img src={workshopIcon} alt="test workshop" />
            </div>

        </div>
    );
};

export default Map;
