# gui

A Clojure library that serves as a meta-library/wrapper around other
libraries and convenience packages, to ensure the widest range of GUI
and utility support across Java versions and operating systems.

In general, I plan to ensure compatability with all JRE from Java 8 to
Java 12+ for the packages that are installed as dependencies (this
obviously means versions of some packages will be out of date -
usually that's fine, particularly if we weren't using some feature of
them anyways).

The main bullet-points I want to hit with this are common things that
many GUI interfaces will tend to require, but may not cleanly fall
into the Clojure built in facilities:

- GUI rendering
- GUI Theming/Styling
- Networking + Serialization
- Logging
- Data Persistence (config file based)

![light](https://github.com/ahungry/gui/blob/master/light-gui.png)
![dark](https://github.com/ahungry/gui/blob/master/dark-gui.png)

# Chosen packages/versions

These have been tested and known to work, on a range of JRE
environments, from Windows 7 (32 bit) + Oracle JRE 8 (last 32 Oracle
one), to Win7 + Zulu OpenJDK JRE 8 to 12, as well as Arch Linux and
Ubuntu OpenJDKs 8 to 12.

## Main layout/GUI interface: Swing + Clojure Seesaw

See notes for some details into why this sort of won by default.

- Clojure Seesaw: https://github.com/daveray/seesaw
- Styles + enhancements to swing: https://github.com/kirill-grouchnikov/radiance

Some of the packages (radiance) had to be installed at their 1.x
versions, as they are the last ones that had maven jars compiled to
work with Java API version 52 (aka, Java 8).

Radiance has a great theming tool, that you can see in the demo when
you boot this app up.

# Notes

Considered but didn't work out:

- JavaFX is a slick library, but support for it across Java versions
  is very hard to align.  Oracle Java 8 ships with it built in,
  OpenJDK 8 ships it as a separate package on GNU/Linux
  distributions.  Later versions of Java (9 and 10) don't seem to have
  it in a clear location.  Java 11 has it as a separate Maven
  installable library.  It might be a good choice for a cross-version
  / OS jar, *if* you get to dictate what Java version your potential
  userbase must use (or if it was for an internal app where you
  provision the appropriate software on the target machines).
- cljfx (https://github.com/cljfx/cljfx) is a great library, but has
  an intentional design choice to only support the latest underlying
  systems.  As such, it was complicated to get working on Arch Linux
  as well as Windows.  The fact it is closely tied to JavaFX also
  makes it difficult to use.
- fn-fx (https://github.com/fn-fx/fn-fx) has similar problems to
  cljfx - very hard to use across a wide-berth of targets (see the
  usage matrix on their github readme for a testament to that - way
  too many red Xs)

## Notes - Setting up profile to test different java version

On ArchLinux you can use `sudo archlinux-java set java-8-openjdk/jre`
or `sudo archlinux-java set java-12-openjdk` (Ubuntu has an update
alternatives approach).

On Windows, you can adjust in your ~/.lein/profile.clj by adding
something like this:

```clojure
{:user
  {
    :java-cmd "C:\\Program\ Files\\Java\\jdk1.8.0_202\\bin\\java.exe "
    ;; :java-cmd "C:\\Users\\IEUser\\scoop\\apps\\zulu12\\current\\bin\\java.exe"
  }
}
```

# TODO

- Integrate xdg-rc package (add windows support there) to allow the
  'default' gui view / package to easily incorporate the radiant theme
  switcher and save user setting for this.
- Figure out keybind to quickly cycle through active tabs (alt+N to go
  to that tab, ctrl + tab / ctrl + shift + tab to iterate across)
- Figure out how to add X to tab to close it out

# License

Copyright © 2019 Matthew Carter <m@ahungry.com>

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
