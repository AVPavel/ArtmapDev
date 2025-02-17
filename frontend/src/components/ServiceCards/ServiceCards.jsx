import React, { useRef } from 'react';
import styles from './ServiceCards.module.css';
import ServiceCard from "./ServiceCard/ServiceCard";
import LogoMusic from '../../assets/images/HomePage/muzic.svg';
import LogoTheater from '../../assets/images/HomePage/teatru.svg';
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
            image: LogoMusic,
            title: "1",
            date: "24 August 2024",
            address: "Stadionul National, Bucuresti"
        },
        {
            image: LogoTheater,
            title: "2",
            date: "15 Septembrie 2024",
            address: "Teatrul National, Bucuresti"
        },
        {
            image: LogoMusic,
            title: "3",
            date: "24 August 2024",
            address: "Stadionul National, Bucuresti"
        },
        {
            image: LogoTheater,
            title: "4",
            date: "15 Septembrie 2024",
            address: "Teatrul National, Bucuresti"
        },
        {
            image: LogoTheater,
            title: "5",
            date: "15 Septembrie 2024",
            address: "Teatrul National, Bucuresti"
        },
        {
            image: LogoTheater,
            title: "6",
            date: "15 Septembrie 2024",
            address: "Teatrul National, Bucuresti"
        },
        {
            image: LogoTheater,
            title: "7",
            date: "15 Septembrie 2024",
            address: "Teatrul National, Bucuresti"
        },

    ];

    return (
        <div className={styles.carouselContainer}>
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