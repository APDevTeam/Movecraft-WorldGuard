# Movecraft-WorldGuard
![WorldGuard](https://github.com/APDevTeam/Movecraft-WorldGuard/actions/workflows/maven.yml/badge.svg)

Home of the code for the following features:
 - WorldGuard plugin integration

## Version support
The `1.10.2` branch is coded for Movecraft 7.0.0 and WorldGuard v6.1.x for 1.10.2.  Support for this version is only for critical issues.

The `1.12.2` branch is coded for Movecraft 7.0.0 and WorldGuard v6.2.x for 1.12.2.

The `main` branch is coded for Movecraft 8.x.x and WorldGuard v7.x.x for 1.14+.

## Download

Devevlopment builds can be found on the [Releases page](https://github.com/APDevTeam/Movecraft-WorldGuard) of this repository.  Stable builds can be found on [our SpigotMC page](TBD).

## Building
This plugin requires that the user setup and build their [Movecraft](https://github.com/APDevTeam/Movecraft) development environment, and then clone this into the same folder as your Movecraft development environment such that Movecraft-WorldGuard and Movecraft are contained in the same folder.  This plugin also requires you to build the latest version of 1.14.4 using build tools.

```
java -jar BuildTools.jar --rev 1.14.4
```

Then, run the following to build Movecraft-Towny through `maven`.
```
mvn clean install
```
Jars are located in `/target`.


## Support
[Github Issues](https://github.com/APDevTeam/Movecraft-WorldGuard/issues)

[Discord](http://bit.ly/JoinAP-Dev)

The plugin is released here under the GNU General Public License V3. 
