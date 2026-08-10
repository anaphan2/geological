from argparse import ArgumentParser
from pathlib import Path

TEMPLATE_MOD_ID = "examplemod"
TEMPLATE_MOD_NAME = "ExampleMod"
TEMPLATE_PACKAGE = "examplegroup.examplemod"


SOURCE_SETS = [
    "common/src/main/kotlin",
    "common/src/main/java",

    "fabric/src/main/kotlin",
    "fabric/src/main/java",

    "neoforge/src/main/kotlin",
    "neoforge/src/main/java",
]

TEXT_EXTENSIONS = {
    ".kt",
    ".java",
    ".json",
    ".toml",
    ".properties",
    ".md",
    ".kts",
    ".gradle",
    ".xml",
    ".txt",
}

parser = ArgumentParser()

parser.add_argument(
    "--package",
    required=True,
    help="New package name"
)

parser.add_argument(
    "--mod-id",
    required=True,
    help="New mod id"
)

parser.add_argument(
    "--mod-name",
    required=True,
    help="New main class name"
)

args = parser.parse_args()

NEW_PACKAGE = args.package
NEW_MOD_ID = args.mod_id
NEW_MOD_NAME = args.mod_name

def package_to_path(package: str) -> Path:
    return Path(*package.split("."))


def is_text_file(path: Path):
    if "META-INF" in path.parts and "services" in path.parts:
        return True

    return path.suffix in TEXT_EXTENSIONS

def replace_in_file(path: Path, replacements: dict[str, str]):
    try:
        text = path.read_text(encoding="utf-8")

    except UnicodeDecodeError:
        return

    for old, new in sorted(
            replacements.items(),
            key=lambda x: len(x[0]),
            reverse=True
    ):
        text = text.replace(old, new)

    path.write_text(
        text,
        encoding="utf-8"
    )

def rename_package(
    root: Path,
    old_package: str,
    new_package: str
):
    old_path = root / package_to_path(old_package)
    new_path = root / package_to_path(new_package)


    if not old_path.exists():
        return

    print(
        f"Moving package {old_path} -> {new_path}"
    )

    new_path.parent.mkdir(
        parents=True,
        exist_ok=True
    )

    old_path.rename(new_path)

def rename_packages():
    for source in SOURCE_SETS:

        rename_package(
            Path(source),
            TEMPLATE_PACKAGE,
            NEW_PACKAGE
        )

def rename_paths():
    replacements = {
        TEMPLATE_PACKAGE: NEW_PACKAGE,
        TEMPLATE_MOD_NAME: NEW_MOD_NAME,
        TEMPLATE_MOD_ID: NEW_MOD_ID,
    }

    for path in sorted(
        Path(".").rglob("*"),
        key=lambda p: len(str(p)),
        reverse=True
    ):

        if not path.exists():
            continue

        new_name = path.name

        for old, new in sorted(
                replacements.items(),
                key=lambda x: len(x[0]),
                reverse=True
        ):
            new_name = new_name.replace(
                old,
                new
            )

        if new_name != path.name:
            new_path = path.with_name(new_name)
            print(
                f"Renaming {path} -> {new_path}"
            )
            path.rename(new_path)

def replace_contents():
    replacements = {
        TEMPLATE_PACKAGE: NEW_PACKAGE,
        TEMPLATE_MOD_NAME: NEW_MOD_NAME,
        TEMPLATE_MOD_ID: NEW_MOD_ID,
    }

    for file in Path(".").rglob("*"):

        if (
            file.is_file()
            and is_text_file(file)
        ):
            replace_in_file(
                file,
                replacements
            )

rename_packages()
rename_paths()
replace_contents()
