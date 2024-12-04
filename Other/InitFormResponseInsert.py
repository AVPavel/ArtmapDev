import pandas as pd

# Citire fișier Excel cu noile denumiri ale coloanelor
try:
    df = pd.read_excel('Artmap.xlsx')
    print("Fișierul Excel a fost citit cu succes!")
except FileNotFoundError:
    print("Fișierul Excel nu a fost găsit. Verificați locația fișierului.")
    exit()

sql_statements = []
print("Începem procesarea rândurilor din Excel...")

# Adaugă începutul tranzacției
sql_statements.append("START TRANSACTION;")

# Generare SQL pentru fiecare rând
for idx, row in df.iterrows():
    print(f"Procesăm rândul {idx + 1}...")

    # Log pentru a verifica numele coloanelor disponibile
    if idx == 0:
        print(f"Coloanele disponibile în fișier: {df.columns.tolist()}")

    try:
        user_id = idx + 1001  # Începem de la 1001 pentru utilizatori generici
        
        # Adăugare utilizator
        sql_statements.append(f"INSERT INTO users (id, username, password, email, role) "
                              f"SELECT {user_id}, 'user_{idx+1}', 'password', 'user_{idx+1}@example.com', 'USER' "
                              f"WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = {user_id});")
        
        # Concerte
        if row['Te atrag concertele ?'] == 'Da' and pd.notna(row['Ce genuri de muzica']):
            genres = row['Ce genuri de muzica'].split(', ')
            for genre in genres:
                sql_statements.append(f"INSERT INTO user_preferences (user_id, category_id, genre_id) "
                                      f"SELECT {user_id}, (SELECT id FROM categories WHERE name = 'Concerte'), "
                                      f"(SELECT id FROM genres WHERE name = '{genre}') "
                                      f"WHERE NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                                      f"AND category_id = (SELECT id FROM categories WHERE name = 'Concerte') "
                                      f"AND genre_id = (SELECT id FROM genres WHERE name = '{genre}'));")
        
        # Teatru
        if row['Îți place să mergi la piese de teatru'] == 'Da' and pd.notna(row['Ce genuri te interesează cel mai mult']):
            genres = row['Ce genuri te interesează cel mai mult'].split(', ')
            for genre in genres:
                sql_statements.append(f"INSERT INTO user_preferences (user_id, category_id, genre_id) "
                                      f"SELECT {user_id}, (SELECT id FROM categories WHERE name = 'Teatru'), "
                                      f"(SELECT id FROM genres WHERE name = '{genre}') "
                                      f"WHERE NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                                      f"AND category_id = (SELECT id FROM categories WHERE name = 'Teatru') "
                                      f"AND genre_id = (SELECT id FROM genres WHERE name = '{genre}'));")
        
        # Festivaluri
        if row['Îți place sa participi la festivaluri'] == 'Da' and pd.notna(row['Ce genuri de festivaluri iți plac cel mai mult']):
            genres = row['Ce genuri de festivaluri iți plac cel mai mult'].split(', ')
            for genre in genres:
                sql_statements.append(f"INSERT INTO user_preferences (user_id, category_id, genre_id) "
                                      f"SELECT {user_id}, (SELECT id FROM categories WHERE name = 'Festivaluri'), "
                                      f"(SELECT id FROM genres WHERE name = '{genre}') "
                                      f"WHERE NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                                      f"AND category_id = (SELECT id FROM categories WHERE name = 'Festivaluri') "
                                      f"AND genre_id = (SELECT id FROM genres WHERE name = '{genre}'));")
        
        # Alte tipuri de evenimente
        if pd.notna(row['Selectează alte tipuri de evenimente care te atrag']):
            events = row['Selectează alte tipuri de evenimente care te atrag'].split(', ')
            for event in events:
                sql_statements.append(f"INSERT INTO user_preferences (user_id, category_id, genre_id) "
                                      f"SELECT {user_id}, (SELECT id FROM categories WHERE name = '{event.strip()}'), NULL "
                                      f"WHERE NOT EXISTS (SELECT 1 FROM user_preferences WHERE user_id = {user_id} "
                                      f"AND category_id = (SELECT id FROM categories WHERE name = '{event.strip()}'));")
    
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
