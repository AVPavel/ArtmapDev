import React from 'react';
import styles from './ServiceCards.module.css'
import ServiceCard from "./ServiceCard/ServiceCard";
import LogoMusic from '../../assets/images/HomePage/muzic.svg'
import LogoTheater from '../../assets/images/HomePage/teatru.svg'
import LogoArt from '../../assets/images/HomePage/arta.svg'

const ServiceCards = () => {
    const services = [
        {icon: LogoMusic, description: "Concerte" },
        {icon: LogoTheater, description: "Piese de teatru" },
        {icon: LogoArt, description: "Expozitii de arta" },
        {icon: LogoArt, description: "Expozitii de arta" }
    ];
    return (
        <div className={styles.cardsWrapper}>
            {services.map((service, index) =>(
                <ServiceCard
                    key={index}
                    icon={service.icon}
                    description={service.description} />
            ))}
        </div>
    )
}

export default ServiceCards;