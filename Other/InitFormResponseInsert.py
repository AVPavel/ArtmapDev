import pandas as pd
from unidecode import unidecode
import re

def escape_sql(value):
    if value is None:
        return 'NULL'
    return f"'{value.replace('\'', '\'\'')}'"

def remove_parentheses(text):
    """
    Elimină orice text din paranteze, inclusiv parantezele.
    Exemplu: 'Workshop-uri (Design, programare, fotografie, etc.)' -> 'Workshop-uri'
    """
    return re.sub(r'\s*\(.*?\)', '', text)

# Definirea setului de valori de excludere
exclude_values = {'etc', 'etc.', 'etc.)', 'etc)'}

# Citire fișier Excel
try:
    df = pd.read_excel('Artmap.xlsx')
    print("Fișierul Excel a fost citit cu succes!")
except FileNotFoundError:
    print("Fișierul Excel nu a fost găsit. Verificați locația fișierului.")
    exit()

# Curățarea datelor din coloane relevante
df.columns = df.columns.str.strip()
relevant_columns = [
    'Te atrag concertele ?',
    'Ce genuri de muzica',
    'Îți place să mergi la piese de teatru',
    'Ce genuri te interesează cel mai mult',
    'Îți place sa participi la festivaluri',
    'Ce genuri de festivaluri iți plac cel mai mult',
    'Selectează alte tipuri de evenimente care te atrag'
]
for col in relevant_columns:
    if col in df.columns:
        df[col] = df[col].astype(str).str.strip()

sql_statements = []
print("Începem procesarea rândurilor din Excel...")

# Adaugă începutul tranzacției
sql_statements.append("START TRANSACTION;")

# Generare SQL pentru fiecare rând
for idx, row in df.iterrows():
    print(f"Procesăm rândul {idx + 1}...")

    try:
        user_id = idx + 1001  # Începem de la 1001 pentru utilizatori generici

        # Adăugare utilizator
        sql_statements.append(
            f"INSERT INTO users (id, username, password, email, role) "
            f"SELECT {user_id}, 'user_{idx+1}', 'password', 'user_{idx+1}@example.com', 'USER' "
            f"WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = {user_id});"
        )

        # Concerte
        if row['Te atrag concertele ?'].lower() == 'da' and pd.notna(row['Ce genuri de muzica']):
            genres_str = remove_parentheses(row['Ce genuri de muzica'])
            genres = [unidecode(genre.strip()) for genre in genres_str.split(',')]
            for genre in genres:
                if genre.lower() in exclude_values:
                    print(f"Excludem genul nedorit: {genre}")
                    continue
                if genre.lower() == 'nan' or not genre:
                    print(f"Excludem genul invalid: {genre}")
                    continue
                genre_escaped = escape_sql(genre)
                sql_statements.append(
                    f"INSERT INTO user_preferences (user_id, category_id, genre) "
                    f"SELECT {user_id}, (SELECT id FROM categories WHERE name = 'Concert'), "
                    f"(SELECT id FROM genres WHERE name = {genre_escaped}) "
                    f"WHERE NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                    f"AND category_id = (SELECT id FROM categories WHERE name = 'Concert') "
                    f"AND genre = (SELECT id FROM genres WHERE name = {genre_escaped}));"
                )

        # Teatru
        if row['Îți place să mergi la piese de teatru'].lower() == 'da' and pd.notna(row['Ce genuri te interesează cel mai mult']):
            genres_str = remove_parentheses(row['Ce genuri te interesează cel mai mult'])
            genres = [unidecode(genre.strip()) for genre in genres_str.split(',')]
            for genre in genres:
                if genre.lower() in exclude_values:
                    print(f"Excludem genul nedorit: {genre}")
                    continue
                if genre.lower() == 'nan' or not genre:
                    print(f"Excludem genul invalid: {genre}")
                    continue
                genre_escaped = escape_sql(genre)
                sql_statements.append(
                    f"INSERT INTO user_preferences (user_id, category_id, genre) "
                    f"SELECT {user_id}, (SELECT id FROM categories WHERE name = 'Teatru'), "
                    f"(SELECT id FROM genres WHERE name = {genre_escaped}) "
                    f"WHERE NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                    f"AND category_id = (SELECT id FROM categories WHERE name = 'Teatru') "
                    f"AND genre = (SELECT id FROM genres WHERE name = {genre_escaped}));"
                )

        # Festivaluri
        if row['Îți place sa participi la festivaluri'].lower() == 'da' and pd.notna(row['Ce genuri de festivaluri iți plac cel mai mult']):
            genres_str = remove_parentheses(row['Ce genuri de festivaluri iți plac cel mai mult'])
            genres = [unidecode(genre.strip()) for genre in genres_str.split(',')]
            for genre in genres:
                if genre.lower() in exclude_values:
                    print(f"Excludem genul nedorit: {genre}")
                    continue
                if genre.lower() == 'nan' or not genre:
                    print(f"Excludem genul invalid: {genre}")
                    continue
                genre_escaped = escape_sql(genre)
                sql_statements.append(
                    f"INSERT INTO user_preferences (user_id, category_id, genre) "
                    f"SELECT {user_id}, (SELECT id FROM categories WHERE name = 'Festival'), "
                    f"(SELECT id FROM genres WHERE name = {genre_escaped}) "
                    f"WHERE NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                    f"AND category_id = (SELECT id FROM categories WHERE name = 'Festival') "
                    f"AND genre = (SELECT id FROM genres WHERE name = {genre_escaped}));"
                )

        # Alte tipuri de evenimente
        if pd.notna(row['Selectează alte tipuri de evenimente care te atrag']):
            events_str = remove_parentheses(row['Selectează alte tipuri de evenimente care te atrag'])
            events = [unidecode(event.strip()) for event in events_str.split(',')]
            for event in events:
                if event.lower() in exclude_values:
                    print(f"Excludem evenimentul nedorit: {event}")
                    continue
                if event.lower() == 'nan' or not event:
                    print(f"Excludem evenimentul invalid: {event}")
                    continue
                event_escaped = escape_sql(event)
                sql_statements.append(
                    f"INSERT INTO user_preferences (user_id, category_id, genre) "
                    f"SELECT {user_id}, (SELECT id FROM categories WHERE name = {event_escaped}), 999 "
                    f"WHERE EXISTS (SELECT 1 FROM categories WHERE name = {event_escaped}) "
                    f"AND NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                    f"AND category_id = (SELECT id FROM categories WHERE name = {event_escaped}));"
                )

    except KeyError as e:
        print(f"Eroare: Coloana {e} nu a fost găsită în fișierul Excel. Verificați denumirile coloanelor.")
        exit()
    except Exception as e:
        print(f"A apărut o eroare neașteptată la rândul {idx + 1}: {e}")
        exit()

# Adaugă finalizarea tranzacției
sql_statements.append("COMMIT;")

# Scriere în fișier SQL
try:
    with open('insert_user_preferences.sql', 'w', encoding="utf-8") as f:
        f.write('\n'.join(sql_statements))
    print("SQL statements generated and saved to 'insert_user_preferences.sql'")
except Exception as e:
    print(f"Eroare la scrierea în fișierul SQL: {e}")
