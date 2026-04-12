import sys
from pathlib import Path

def create_package_by_path(path, base_directory):
    relative_path = path.relative_to(base_directory)
    package = '.'.join(relative_path.parts)
    return f'package {package}'

def rename_package(directory):
    for path in Path(directory).rglob('*.kt'):
        with open(path, 'r', encoding='utf-8') as file:
            content = file.read()
        
        package = content.splitlines()[0]

        current_path = path.parent
        new_package = create_package_by_path(current_path, directory)

        print(f"Renaming package in {path} from '{package}' to '{new_package}'")

        new_content = content.replace(package, new_package, 1)
        
        with open(path, 'w', encoding='utf-8') as file:
            file.write(new_content)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python rename_package.py <directory>")
        sys.exit(1)

    directory = sys.argv[1]
    rename_package(directory)