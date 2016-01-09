# Source code for NEScala web site.

## Current maintainers

* [@bmc](https://github.com/bmc)
* [@sethtisue](https://github.com/sethtisue)
* [@softprops](https://github.com/softprops)

## Prerequisites

* You'll need to install [SBT](http://scala-sbt.org/)
* You'll want a decent editor or IDE.
* You'll need to understand at least a little bit about
  [Unfiltered](http://unfiltered.databinder.net/Unfiltered.html).
* You'll have to wrap your brain around the occasionally confusing source
  code layout. If you're having trouble grokking it, contact one of the
  maintainers (above).
  
## Building and running locally

### `meetup.properties`

Before you deploy the site (see below), you _must_ ensure that you can run
it locally. Doing so requires a `meetup.properties` file (which is _never_
committed, for security reasons). Contact one of the maintainers to get a
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

Within `sbt`

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

If it's website content you want to update, the templates for the
current year are in 
[`src/main/scala/philly2016/Templates.scala`](blob/master/src/main/scala/philly2016/Templates.scala).

If you need to hack on the CSS rules, the current year uses
[LESS](http://lesscss.org), while previous years use straight CSS.
The Philly 2016 LESS rules are in 
[`src/main/assets/css/philly2016.less`](blob/master/src/main/assets/css/philly2016.less).
If you don't understand LESS, **don't hack the CSS until you do.**
