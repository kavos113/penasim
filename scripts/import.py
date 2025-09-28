import sqlite3
import os
import sys
from glob import glob
import pandas as pd

def insert_csv(db_path, csv_dir):
    csv_files = glob(os.path.join(csv_dir, '*.csv'))

    for csv in csv_files:
        table_name = os.path.splitext(os.path.basename(csv))[0]
        df = pd.read_csv(csv)

        with sqlite3.connect(db_path) as conn:
            cursor = conn.cursor()
            cursor.execute(f'DELETE FROM {table_name}')
            
            df.to_sql(table_name, conn, if_exists='append', index=False)
            print(f'Inserted {len(df)} records into {table_name}')

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: python import.py <db_path> <csv_dir>")
        sys.exit(1)

    db_path = sys.argv[1]
    csv_dir = sys.argv[2]
    insert_csv(db_path, csv_dir)