import React from 'react';
import styles from './ImageComponent.module.css'

const ImageComponent = ({ src, alt, overlayContent, isCentered }) => {
    return (
        <div className={`${styles.imageContainer} ${styles.isCentered ? "centered" : ""}`}>
            <img src={src} alt={alt} className="image" />
            {overlayContent && (
                <div className="overlay">
                    {overlayContent}
                </div>
            )}
        </div>
    );
};

export default ImageComponent;