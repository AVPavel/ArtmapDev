import React from 'react';
import styles from './ServiceCards.module.css'
import ServiceCard from "./ServiceCard/ServiceCard";
import Logo from '../../assets/images/SimpleLogo.svg'

const ServiceCards = () => {
    return (
        <div className={styles.cards}>
            <ServiceCard icon={Logo} description="Concerte"/>
            <ServiceCard icon={Logo} description="Piese de teatru"/>
            <ServiceCard icon={Logo} description="Expozitii de arta"/>
        </div>
    )
}

export default ServiceCards;