import styles from './WhyArtmapSection.module.css'

const WhyArtmapSection = () => {
    return (
        <div className={styles.WhyArtmapSectionContainer}>
            <div className={styles.leftSection}>
                <p className={styles.redWriting}>Here is the perfect place for all types of events</p>
                <h1 className={styles.question}>Why plan your activities on ArtMap?</h1>
                <p className={styles.descriptionParagraph}>Because it provides a personalized, location-based experience for discovering all kinds of events near them.</p>
                <p className={styles.descriptionParagraph}>With a user-friendly interface, real-time updates, and smart filtering, ArtMap makes finding and attending events effortless. Whether it's concerts, exhibitions, workshops, or festivals, ArtMap helps users explore their local culture and social scene with ease.</p>
                <button>Search</button>
            </div>
            <div className={styles.rightSection}>

            </div>
        </div>
    )
}

export default WhyArtmapSection;