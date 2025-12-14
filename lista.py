import requests
import csv
import time

def get_all_spells():
    """Obtiene todos los conjuros de D&D 5e desde la API"""
    try:
        # Obtener lista de todos los conjuros
        response = requests.get('https://www.dnd5eapi.co/api/spells')
        spells_list = response.json()
        
        spells_data = []
        
        for spell in spells_list['results']:
            # Obtener detalles de cada conjuro
            spell_detail = requests.get(f'https://www.dnd5eapi.co{spell["url"]}').json()
            
            # Procesar la descripción
            description = ' '.join(spell_detail.get('desc', []))
            
            # Procesar mejoras por nivel superior
            higher_level = ' '.join(spell_detail.get('higher_level', []))
            if higher_level:
                description += f" Mejora por nivel superior: {higher_level}"
            
            # Procesar clases
            classes = ', '.join([c['name'] for c in spell_detail.get('classes', [])])
            
            spell_data = {
                'Nombre': spell_detail.get('name', ''),
                'Escuela': spell_detail.get('school', {}).get('name', ''),
                'Nivel': spell_detail.get('level', 0),
                'Clases': classes,
                'Descripcion': description
            }
            
            spells_data.append(spell_data)
            time.sleep(0.1)  # Para no sobrecargar la API
            
        return spells_data
        
    except Exception as e:
        print(f"Error: {e}")
        return []

def save_to_csv(spells_data, filename='conjuros_dnd_5e.csv'):
    """Guarda los datos en un archivo CSV"""
    with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['Nombre', 'Escuela', 'Nivel', 'Clases', 'Descripcion']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        
        writer.writeheader()
        for spell in spells_data:
            writer.writerow(spell)

# Ejecutar el script
if __name__ == "__main__":
    print("Obteniendo conjuros de D&D 5e...")
    spells = get_all_spells()
    
    if spells:
        save_to_csv(spells)
        print(f"Se han guardado {len(spells)} conjuros en 'conjuros_dnd_5e.csv'")
    else:
        print("No se pudieron obtener los conjuros")