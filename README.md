**zAntiAutoClick** is a plugin designed to analyze players' click sessions on entities. At the end of each session, the plugin automatically determines whether the player's behavior appears valid or suspicious.
Its main purpose is to provide detailed insights into player activity, helping you detect potential use of auto-clicking tools.

**Note**: zAntiAutoClick does not apply any automatic punishments. It is up to you to review the data and take appropriate action if cheating is detected.

# Requirements:
- [zMenu](https://modrinth.com/plugin/zmenu)
- [PacketEvent](https://modrinth.com/plugin/packetevents)

# Features
- Detect automatic clicking (autoclickers) when players attack entities
- Send a Discord webhook to notify your team of session results
- Notify your team in-game with session result messages
- Fully configurable analysis methods
- Inventory GUI to view all player sessions
- Inventory GUI to review sessions flagged for cheating
- Supports both SQLite and MySQL for data storage

# How does it work?
The plugin listens to attack packets sent by players when they hit an entity and records the time difference between each click. These clicks are grouped into what we call a session. A session represents all the time intervals between clicks over a certain period. For a session to be valid, it must contain a minimum number of clicks and last a minimum duration — both of which can be configured.

Once the session ends, the plugin analyzes it by checking various key indicators to determine whether the behavior is human or likely automated. It then calculates a suspicion percentage: if it exceeds 60%, the player is flagged as suspicious.

You can then review your players' sessions and take action against cheaters.
One of the most effective indicators is the standard deviation of the click intervals. The lower it is, the more consistent the clicking pattern — a common sign of an autoclicker. If the standard deviation is below 20, the player is very likely cheating.