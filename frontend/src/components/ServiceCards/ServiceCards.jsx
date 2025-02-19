import React, { useRef } from 'react';
import styles from './ServiceCards.module.css';
import ServiceCard from "./ServiceCard/ServiceCard";
import PozaOpera from '../../assets/images/HomePage/Poza_opera_articole.jpg';
import PozeExpozitie from '../../assets/images/HomePage/poza_expozitie_articole.jpg';
import PozaConcert from '../../assets/images/HomePage/Poza_concert_articole.jpg';
import PozaBalet from '../../assets/images/HomePage/Poza_balet_articole.jpg';
import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/navigation';

const ServiceCards = () => {
    const swiperRef = useRef();
    const prevRef = useRef();
    const nextRef = useRef();

    const services = [
        {
            image: PozaOpera,
            title: "Orchestra din Lyon vine in București!",
            date: "04.12.2024",
            address: "Bulevardul Mihail Kogălniceanu 70-72, București 050104"
        },
        {
            image: PozaBalet,
            title: "Balet Romeo si Julieta",
            date: "05.04.2025",
            address: "Bulevardul Mihail Kogălniceanu 70-72, București 050104"
        },
        {
            image: PozeExpozitie,
            title: "Expoziție nouă la MNAR",
            date: "04.12.2024",
            address: "Bulevardul Mihail Kogălniceanu 70-72, București 050104"
        },
        {
            image: PozaConcert,
            title: "AC/DC Sosește la Arene Romane",
            date: "09.12.2025",
            address: "Bulevardul Mihail Kogălniceanu 70-72, București 050104"
        },
        {
            image: PozaConcert,
            title: "AC/DC Sosește la Arene Romane",
            date: "09.12.2025",
            address: "Bulevardul Mihail Kogălniceanu 70-72, București 050104"
        }
    ];


    return (

        <div className={styles.carouselContainer}>
            <h2 className={styles.ArticlesTitle}>Articles</h2>
            <button
                ref={prevRef}
                className={`${styles.arrowButton} ${styles.leftArrow}`}
            >
                <FiChevronLeft />
            </button>

            <Swiper
                ref={swiperRef}
                modules={[Navigation]}
                spaceBetween={20}  // Reduced spacing
                slidesPerView={'auto'}
                navigation={{
                    prevEl: prevRef.current,
                    nextEl: nextRef.current,
                }}
                onBeforeInit={(swiper) => {
                    swiper.params.navigation.prevEl = prevRef.current;
                    swiper.params.navigation.nextEl = nextRef.current;
                }}
                breakpoints={{
                    320: { slidesPerView: 1 },
                    768: { slidesPerView: 2 },
                    1024: { slidesPerView: 3 },
                    1280: { slidesPerView: 4 }
                }}
                loop={true}
                speed={400}
                grabCursor={true}
                centeredSlides={false}
                className={styles.swiperContainer}
            >
                {services.map((service, index) => (
                    <SwiperSlide key={index} className={styles.swiperSlide}>
                        <ServiceCard
                            image={service.image}
                            title={service.title}
                            date={service.date}
                            address={service.address}
                            className={styles.card}
                        />
                    </SwiperSlide>
                ))}
            </Swiper>

            <button
                ref={nextRef}
                className={`${styles.arrowButton} ${styles.rightArrow}`}
            >
                <FiChevronRight />
            </button>
        </div>
    )
}

export default ServiceCards;