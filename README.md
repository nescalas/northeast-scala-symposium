# Source code for [NEScala web site][NEScala] [![Build Status](https://travis-ci.org/nescalas/northeast-scala-symposium.svg?branch=travis)](https://travis-ci.org/nescalas/northeast-scala-symposium)

[NEScala]: http://www.nescala.org/

## Current maintainers

* [@bmc](https://github.com/bmc)
* [@sethtisue](https://github.com/sethtisue)
* [@softprops](https://github.com/softprops)

[maintainers]: #current-maintainers

## Prerequisites

* You'll need to install [SBT](http://scala-sbt.org/)
* You'll want a decent editor or IDE.
* You'll need to understand at least a little bit about
  [Unfiltered](http://unfiltered.databinder.net/Unfiltered.html).
* You'll have to wrap your brain around the occasionally confusing source
  code layout. If you're having trouble grokking it, contact one of the
  [maintainers][].

## Building and running locally

### `meetup.properties`

Before you [deploy the site](#going-live), you _must_ ensure that you can run
it locally. Doing so requires a `meetup.properties` file (which is _never_
committed, for security reasons). Contact one of the [maintainers][] to get a
working copy, and install that copy in `src/main/resources`. (The
`.gitignore` file ensures that it'll never be committed.)

When running live, this file isn't necessary, because Heroku configuration
properties are used, instead.

### It's all done with SBT

#### Compiling

    $ sbt
    ...
    > ;clean;compile
    ...

#### Running

You can run the server in two ways.

##### Manually

With the `run` command in `sbt`:

    > run

will run on port 8080. You can also specify a port number:

    > run 9090

To stop the run, just hit ENTER.

##### Via `sbt-revolver`

You can also use `sbt-revolver`. To start:

    > re-start

or

    > re-start 9090

To stop:

    > re-stop

## The content

* **Web content**: If it's web site content you want to update, the templates
  for the current year are in
  [`src/main/scala/philly2016/Templates.scala`](src/main/scala/philly2016/Templates.scala).

* **CSS**: If you need to hack on the CSS rules, the 2016 code uses
  [LESS](http://lesscss.org), while previous years use straight CSS.
  The Philly 2016 LESS rules are in
  [`src/main/assets/css/philly2016.less`](src/main/assets/css/philly2016.less).
  If you don't understand LESS, **don't hack the CSS until
  [you do](http://lesscss.org/features/).**

* **Static resources**: Anything in
  [`src/main/resources/www`](src/main/resources/www) is served as a static
  resource. That is, URL path `/foo.png` corresponds to file
  `src/main/resources/www/foo.png`, and `/www/js/nyc.js` corresponds
  to `src/main/resources/www/js/nyc.js`. (That's why the `favicon.ico` file
  is in `src/main/resources/www`.)

### The 2016 Schedule

For simplicitly, the 2016 schedule is read from a CSV file, which is,
itself, merely a CSV download of a Google Spreadsheet. The code assumes
that the schedule is loadable as resource `/www/2016/Schedule.csv`
(so the current schedule is always located at
[`src/main/resources/2016/Schedule.csv`](src/main/resources/2016/Schedule.csv)
in the code).

The code also assumes that the first line is a header line, and it looks for
these headers, interpreted the associated column values as described:

* "Activity": The short name of the activity. This could be "Lunch", "Break",
  or a talk title.
* "Description": The full description of the entry. This field can contain
  Markdown. It can also be empty.
* "Speaker": The speaker, if the row is for a talk, or empty otherwise.
* "Start": The start time of the entry, in "HH:mm a" format (e.g., "8:30 AM",
  "4:00 PM"). Required.
* "Duration": The duration, in hours and minutes. e.g., "0:40" for 40 minutes,
  "1:15" for an hour and fifteen minutes. Required.
* "Meetup ID": The meetup ID of the speaker, if the row is a talk. Empty
  otherwise.

Any other headers (and corresponding columns) are ignored.

Thus, to update the schedule:

* Edit the Google Spreadsheet.
* Download as CSV.
* Copy to `src/main/resources/www/2016/Schedule.csv`
* Compile and run locally, and verify that the schedule looks right.
* [Deploy](#going-live).

## Going Live

### Getting Access

We're running on Heroku. You won't be able to push to Heroku unless:

* you have a Heroku account, and
* you are listed as a collaborator on the Heroku application.

Contact one of the [maintainers][] for access.

### Preparation

You'll need to install the [Heroku Toolbelt][], and you must use it to login,
as described in the [documentation][Heroku Toolbelt].

Next, you'll need to add a Git remote to your cloned copy of this repo:

    $ git remote add heroku git@heroku.com:nescala.git

### Deploying

Assuming you've properly prepared your environment, you can deploy with
a single Git command:

    $ git push heroku master

If you prefer, you can also deploy from the Heroku dashboard
[Deploy](https://dashboard.heroku.com/apps/nescala/deploy/github) tab. The
application has been linked to the main Git repo. If your changes are pushed to
the main repo, you can deploy them from the _Manual Deploy_ section of the
Deploy tab. (Honestly, though, the command line is faster.)

[Heroku Toolbelt]: https://toolbelt.heroku.com/

## Running on a non-Heroku Server

If you want to run the site on a non-Heroku server (e.g., for testing),
you can build a fat jar and run it:

    $ sbt fatjar

That command builds `target/scala_2.10/root-assembly-0.1-SNAPSHOT.jar`,
which you can run with `java -jar`.
