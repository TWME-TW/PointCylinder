# PointCylinder

PointCylinder is a Minecraft plugin that allows players to create cylinders from three points using WorldEdit. This plugin supports both solid and hollow cylinders.

## Features

- Create solid cylinders from three points
- Create hollow cylinders from three points
- Supports specifying block type, height, and thickness
- Asynchronous operations to prevent server lag

## Installation

1. Download the latest release of PointCylinder from the [releases page](https://github.com/TWME-TW/PointCylinder/releases).
2. Place the downloaded JAR file into your server's `plugins` directory.
3. Restart your server to load the plugin.

## Dependencies

- [WorldEdit](https://enginehub.org/worldedit/)
- [FastAsyncWorldEdit](https://intellectualsites.github.io/FAWE/)

## Commands

### `//pcyl`

Create a solid cylinder from three points.

- **Usage:** `/pcyl <block_type> [height] [-d]`
- **Permission:** `worldedit.generation.pointcylinder`
- **Description:** Allows the player to create a solid cylinder from three points.

### `//phcyl`

Create a hollow cylinder from three points.

- **Usage:** `//phcyl <block_type> [height] [thickness] [-d]`
- **Permission:** `worldedit.generation.pointhollowcylinder`
- **Description:** Allows the player to create a hollow cylinder from three points.

## Permissions

- `worldedit.generation.pointcylinder`: Allows the player to create a solid cylinder from three points. Default: `op`
- `worldedit.generation.pointhollowcylinder`: Allows the player to create a hollow cylinder from three points. Default: `op`

## Usage

1. Select three points using WorldEdit.
2. Use the `//pcyl` or `//phcyl` command with the desired parameters to create the cylinder.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request on GitHub.

## Support

For support, please open an issue on the [GitHub repository](https://github.com/TWME-TW/PointCylinder/issues).

