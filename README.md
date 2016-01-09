# Source code for NEScala web site.

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
[`src/main/scala/philly2016/Templates.scala`](src/main/scala/philly2016/Templates.scala).

If you need to hack on the CSS rules, the current year uses
[LESS](http://lesscss.org), while previous years use straight CSS.
The Philly 2016 LESS rules are in 
[`src/main/assets/css/philly2016.less`](src/main/assets/css/philly2016.less).
If you don't understand LESS, **don't hack the CSS until you do.**

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
