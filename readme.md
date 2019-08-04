### Plugin for Intellij IDEA

Allows to see autocompletion for enums in JSF EL.

By default enums are not supported by JSF EL, but it's possible to use them with extension libraries like PrimeFaces.
Even in that case there is no autocompletion support for enum names and constants. This plugin adds this support.

To use it you have to create enumPlugin.properties file somewhere in project and put there enum descriptions
in format: `<enumName> = <enum.full.class.Name>`

Typing `<enumName>` will trigger autocompletion of enum name and `<enum.full.class.Name>` will allow to find enum constants.
Also if enum constants contain constructor arguments (usually used for description) they will be shown in suggestion description.

It's a basic functionality. You are free to improve it by pull requests.