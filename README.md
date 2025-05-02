# ElmLands_Staff

![Paper 1.21.3](https://img.shields.io/badge/Minecraft-1.21.3-blue)

**ElmLands_Staff** is a robust staff management plugin for Minecraft servers running on Paper 1.21. Tailored for server administrators, it offers a suite of tools for moderating players, managing staff visibility, and ensuring a secure environment. Features include vanish mode, random player teleportation, CPS testing, player freezing, Anti-VPN protection, and more, empowering staff to maintain order and fairness with ease.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Commands & Permissions](#commands--permissions)
- [Configuration](#configuration)
- [Contributing](#contributing)
- [Credits](#credits)

## Features

- **Vanish Mode**: Toggle invisibility (`/vanish`) for discreet monitoring, with an action bar reminder.
- **Staff Teleport**: Randomly teleport to online players (`/staffrtp`) while vanished for quick inspections.
- **CPS Testing**: Measure a player’s clicks per second (`/cpstest <player>`) to detect potential autoclickers.
- **Player Freezing**: Immobilize players (`/freeze <player>`) to prevent movement or interaction during investigations.
- **Chat Management**: Mute global chat (`/mutechat`) with bypass permissions for staff.
- **Night Vision**: Toggle night vision (`/nightvision`) for staff via custom items.
- **Online Staff List**: View online staff members (`/onlinestaff`), with vanished staff visible to authorized users.
- **Environment Protection**: Prevent vanished staff from trampling crops or being targeted by mobs.

## Installation

### Requirements
- Paper 1.21.3 server
- Java 21

## Commands & Permissions
| Command                   | Description                     | Permission                 |
|---------------------------|---------------------------------|----------------------------|
| ``/vanish (or /v)``       | Toggle vanish mode              | elmlands.staff.vanish      |
| ``/staffrtp``             | Teleport to a random player     | elmlands.staff.staffrtp    |
| ``/freeze <player>``      | Freeze/unfreeze a player        | elmlands.staff.freeze      |
| ``/turn <player>``        | Rotate a player 180 degrees     | elmlands.staff.turn        |
| ``/mutechat``             | Mute/unmute global chat         | elmlands.staff.mutechat    |
| ``/onlinestaff``          | List online staff members       | elmlands.staff.onlinestaff |
| ``/elmlandsstaffreload``  | Reload the plugin configuration | elmlands.staff.reload      |
| ``/cpstest <player>``     | Start a CPS test for a player   | elmlands.staff.cpstest     |

## Additional Permissions
- ``elmlands.staff.vanish.see``: View vanished staff in ``/onlinestaff``
- ``elmlands.staff.bypassmute``: Chat while chat is muted.
- ``elmlands.staff.nightvision``: Toggle night vision via custom items.

## Configuration

```
blacklisted_isps:
  - "Cloudflare"
  - "ISP2"
  - "ISP3"
  ```

## Contributing
1. Fork the repository.
2. Create a new branch (git checkout -b feature/your-feature).
3. Make your changes and commit (git commit -m "Add your feature").
4. Push to your branch (git push origin feature/your-feature).
5. Open a pull request.
- Please report bugs or suggest features via the Issues tab.

## Credits
- Developed by TheFonze
- Maintained by djtmk
