import pandas as pd
import datetime
import sys
import os

file_path = '/Users/work/Downloads/trade-202601~20260326.csv'
output_file = '/Users/work/proj/repo/hzapp/hzapp-erplus/hzapp-module-erplus/hzapp-module-erplus-adv/docs/tmp/generated_sql.sql'

def map_type(val):
    if pd.isna(val):
        return "VARCHAR(255)"
    if isinstance(val, (int, complex)):
        return "BIGINT"
    elif isinstance(val, float):
        return "DECIMAL(18, 4)"
    elif isinstance(val, (datetime.datetime, pd.Timestamp)):
        return "DATETIME"
    else:
        return "VARCHAR(255)"

try:
    if not os.path.exists(file_path):
        print(f"Error: File not found: {file_path}")
        sys.exit(1)

    # Try reading as excel, fall back to csv if it's not a zip file (common for CSVs misnamed .xlsx)
    try:
        df = pd.read_excel(file_path)
    except Exception as e:
        if "File is not a zip file" in str(e) or "BadZipFile" in str(e) or "Excel file format" in str(e):
             print(f"Failed to read as excel ({e}), trying as CSV...")
             try:
                 df = pd.read_csv(file_path, encoding='utf-8')
             except UnicodeDecodeError:
                 print("Failed to read as CSV with utf-8, trying with gbk...")
                 df = pd.read_csv(file_path, encoding='gbk')
        else:
             print(f"Excel read error: {repr(e)}")
             raise e

    if df.empty:
        print("Error: The file is empty.")
        sys.exit(1)

    headers = df.columns.tolist()
    
    # Get first non-null row to determine types, or just use the first row
    sample_row = df.iloc[0]

    table_name = "ads_settlement_stats"
    create_table_sql = f"CREATE TABLE IF NOT EXISTS {table_name} (\n"
    create_table_sql += "    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,\n"
    
    cols_sql = []
    for i, header in enumerate(headers):
        if pd.isna(header): continue
        mysql_type = map_type(sample_row.iloc[i])
        cols_sql.append(f"    `{header}` {mysql_type}")
    
    create_table_sql += ",\n".join(cols_sql)
    create_table_sql += "\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;\n\n"

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(create_table_sql)
        
        for _, row in df.iterrows():
            if row.isna().all():
                continue
            
            valid_headers = []
            sql_values = []
            
            for i, (header, val) in enumerate(row.items()):
                if pd.isna(header): continue
                valid_headers.append(f"`{header}`")
                
                if pd.isna(val):
                    sql_values.append("NULL")
                elif isinstance(val, (int, float)):
                    sql_values.append(str(val))
                elif isinstance(val, (datetime.datetime, pd.Timestamp)):
                    sql_values.append(f"'{val.strftime('%Y-%m-%d %H:%M:%S')}'")
                else:
                    val_str = str(val).replace("'", "''")
                    sql_values.append(f"'{val_str}'")
            
            insert_sql = f"INSERT INTO {table_name} ({', '.join(valid_headers)}) VALUES ({', '.join(sql_values)});\n"
            f.write(insert_sql)

    print(f"SQL generated successfully in {output_file}")
    print(f"Processed {len(df)} rows.")

except Exception as e:
    print(f"Error: {repr(e)}")
    import traceback
    traceback.print_exc()
    sys.exit(1)
