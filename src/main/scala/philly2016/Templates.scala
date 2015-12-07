package nescala.philly2016

import nescala.{ Meetup, SessionCookie }
import unfiltered.response.Html5
import java.text.SimpleDateFormat
import java.util.{ Calendar, Date, TimeZone }

trait Templates {

  import Enrichments.EnrichedDate
  import Constants._

  lazy val DefaultDateFormat = new SimpleDateFormat("E MMM d")
  lazy val DayOneTimeStr = DefaultDateFormat.format(Site.dayOneTime.toDate)
  lazy val ProposalCutoffStr = DefaultDateFormat.format(Site.proposalCutoff.toDate)
  lazy val VotesCutoffStr = DefaultDateFormat.format(Site.votesCutoff.toDate)

  private def timestamp(d: Date, offset: Boolean = true) = {
    def fmt(f: String) = new SimpleDateFormat(f) {
      if (offset) {
        setTimeZone(TimeZone.getTimeZone("US/Eastern"))
      }
    }
    val time = (if (d.field(Calendar.MINUTE) > 0) fmt("h:mm") else fmt("h")).format(d)
    <span class="time">{ time }</span><span class="ampm">{ fmt("aa").format(d).toLowerCase }</span>
  }

  /** list of current member's proposals and modal for editing them */
  private def proposed
    (proposals: Iterable[Proposal]): xml.NodeSeq =
    (<h3 id="proposed">Your current talk proposals</h3><ul>{
      proposals.map { p =>
        <li>
         <p>
          <a href="#" class="btn propose"
           data-id={p.id} data-kind={p.kind}
           data-name={p.name} data-desc={p.desc}><i class="fa fa-pencil-square-o"></i> Edit</a>
           { p.kind match {
             case "medium"    =>
               <span class="mute"> 45 min</span>
             case "short"     =>
               <span> 30 min</span>
             case "lightning" =>
               <span> 15 min</span>
           } } <i class="fa fa-clock-o"></i>
          <a href={s"#${p.domId}"} title={p.name}> { p.name } </a>
         </p>
        </li>
      }
    }</ul>
   )

  private def pollsClosed =
    <span>Votes on <a href="/2016/talks#proposals">talk proposals</a><br/>are in; winners below.</span>

  /** every proposal related thing owned by the current member */
  private def propose(proposals: Iterable[Proposal]): xml.NodeSeq = (<section>
    <div class="grid center-on-mobiles" id="propose">
      <div class="unit whole">
       <p>
         Attending is only <i>half</i> the experience.
       </p>
       <p>
         Attendees are encouraged to submit talk proposals that other attendees
         can vote on. You may submit up to <strong>3 talk proposals</strong>.
         If you're shy, don't be afraid: It's a friendly crowd, and a
         15-minute lightning talk goes by faster than you think.
         If you're used to talking for hours, remember to time yourself.
       </p>
        { if (Site.proposalsOpen)
          <p>Proposals will be accepted until {ProposalCutoffStr} 11:59pm EST.</p>
         else
          <p><i class="fa fa-info-circle"></i> Proposals are now closed.</p>
       }
      </div>
      <div id="propose-talk">
        <div id="propose-container" class="unit">
          { proposals.size match {
            case 0 =>
              if (Site.proposalsOpen) <span>
                <p>
                 <a href="#" class="btn propose">Submit a new talk proposal</a>
                </p>
              </span> else <span/>
            case n if n < Proposal.Max =>
              <span>
                { proposed(proposals) }
                {
                  if (Site.proposalsOpen)
                    <p><a href="#" class="btn propose">Submit a new talk proposal</a></p>
                  else
                    <span/>
                }
               </span>
            case n =>
              <span>
                { proposed(proposals) }
              </span>
          } }
        <div class="modal">
         <div class="grid form"></div>
        </div>
        </div>
      </div>
    </div>
  </section>)

  def proposalsPage
   (proposals: Iterable[Proposal])
   (session: Option[SessionCookie] = None) =
    layout(session)(scripts = Seq("/js/2016/proposals.js", "/js/2016/voting.js"))(
      <div class="unit whole align-center lead well">
        <div class="grid">
          <p class="unit whole">
            This year's symposium continues with last year's successful
            mix of talk lengths, to give new attendees a chance to speak up
            and share what's on their minds.
          </p>
        </div>
        <div class="grid">
          <div class="unit one-third">
            <a href="#medium-proposals">Med</a>
            (45 min)
            <p class="small mute">3 slots</p>
          </div>
          <div class="unit one-third">
            <a href="#medium-proposals">Short</a>
            (30 min)
            <p class="small mute">4 slots</p>
          </div>
          <div class="unit one-third">
            <a href="#lightning-proposals">Lightning</a>
            (15 min)
            <p class="small mute">6 slots</p>
          </div>
        </div>
      </div>
    )(
      <div class="inverse whole" id="speak">
        <div class="grid">
          <div class="unit whole">
            <h2>
              <i class="fa fa-bullhorn"></i> Speak up
            </h2>
          </div>
          <div class="unit whole">{
            session match {
              case Some(member) if member.nescalaMember =>
                propose(member.proposals)
              case Some(member) =>
                <p>
                  You can be part of this year's symposium by
                  joining our <a href="http://www.meetup.com/nescala/">Meetup group</a>.
                </p>
              case _ =>
                <p>
                  You can be part of this year's symposium by joining our
                  <a href="http://www.meetup.com/nescala/">Meetup group</a>
                  and <a href="/login?state=propose">logging in</a>
                  to vote or to submit a talk.
                </p>
            }
          }</div>
        </div>
      </div>
      <div class="regular whole" id="proposals">
        <div class="grid">
          <div class="unit whole">
            <h2>
             <i class="fa fa-check-circle-o"></i> Listen up
            </h2>
            <p>
              NE Scala has no program committee. We do not select speakers
              for you to watch and listen to: <strong>You do.</strong>
            </p>
              {
                if (Site.votesOpen) session match {
                  case Some(member) if member.canVote =>
                    <p>You may vote for up to 6 talks you'd like to see for this year's symposium below.</p>
                    <p>Polls close {VotesCutoffStr} at 11:59pm EST.</p>
                    <p id="votes-remaining">
                    { (Proposal.MaxVotes - member.votes.size) match {
                      case 0 =>
                        <span>You have <strong>no votes</strong> remaining</span>
                      case 1 =>
                        <span>You have <strong>one vote</strong> remaining</span>
                      case n =>
                        <span>You have <strong>{n} votes</strong> remaining</span>
                    } }
                    </p>
                  case Some(_) =>
                    <p>
                      Members RSVP'd on
                      <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>meetup.com</a> may
                      vote for the proposed talk they want to see.
                    </p>
                  case _ =>
                    <p>
                      Members RSVP'd on
                      <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>meetup.com</a> may
                      <a class="btn" href="/login?state=proposals">Login</a> and vote for the talk proposals they want to see.
                    </p>
                }
                else
                  <p>
                    Talks are proposed by your peers and in a short time we will open up voting polls for you
                    to select the talks you want to see.
                  </p>
              }
          </div>
          <div class="unit whole">{
            val grouped = proposals.groupBy(_.kind)
            Array("medium", "short", "lightning")
             .map(len => (len, grouped.get(len).getOrElse(Nil))).map {
              case (kind, ps) =>
                <h3 id={s"$kind-proposals"} class="unit whole">
                  { ps.size } <strong>{ kind }</strong> length proposals
                </h3>
                <ul>{ ps.map(proposal(
                  Site.votesOpen && session.exists(_.canVote),
                  session.filter(_ => Site.votesOpen).map(_.votes).getOrElse(Set.empty)))
                }</ul>
            }
          }</div>
        </div>
      </div>)

  private def personal(p: Proposal): xml.NodeSeq = avatar(p) ++ links(p)

  private def avatar(p: Proposal) =
    <a href={ s"http://meetup.com/nescala/members/${p.memberId}"}
      class="circle" style={s"background-image:url(${p.member.get.photo}); background-size: cover; background-position: 50%"}>
    </a>

  private def links(proposal: Proposal) =
    (<div class="links">
     <a class="primary" href={ s"http://meetup.com/nescala/members/${proposal.memberId}" } target="_blank">
       { proposal.member.get.name }
     </a>
      {
        if (proposal.member.get.twttr.isDefined) (
          <p>
            <i class="fa fa-twitter"></i>
            <a class="twttr small" href={ s"http://twitter.com/${proposal.member.get.twttr.get.drop(1)}"} target="_blank">
             { proposal.member.get.twttr.get }
            </a>
          </p>
        )
      }
    </div>)

   /** a single li element for a proposal */
   private def proposal
    (canVote: Boolean, votes: Set[String])(p: Proposal)  =
    <li class="unit whole talk" id={ p.domId }>
    <div class="grid">
      <div class="unit one-fifth">
        { personal(p) }
      </div>
      <div class="unit four-fifths">
        <h2><a href={ "#"+p.domId }>{ p.name }</a></h2>
        <div>
          <span class="mute"><i class="fa fa-clock-o"></i> { p.kind match {
            case "medium" => "45 minutes"
            case "short" => "30 minutes"
            case "lightning" => "15 minutes"
          } }
          </span>
          { if (canVote)
            <p><form class="ballot" action={s"/2016/talks/${p.id}/votes"}
              method={ if (votes.contains(p.id)) "DELETE" else "POST"}>
             <button type="submit" class={s"voting btn ${if (votes.contains(p.id)) "voted-yes" else "" }"}
              disabled={if (!votes.contains(p.id) && (Proposal.MaxVotes - votes.size < 1)) Some(xml.Text("disabled")) else None} >
              { if (votes.contains(p.id)) "Change your mind?" else "Let's make this happen"}
             </button>
            </form></p>
          }
        </div>
        <p class="desc">{ p.desc }</p>
      </div>
    </div>
    <hr/>
  </li>

  def indexPage
   (slots: Seq[Schedule.Slot], sponsors: List[Meetup.Sponsor])
   (session: Option[SessionCookie] = None) =
    layout(session)(scripts = Seq.empty[String])(
      <div class="grid well">
        <div class="unit half right center-on-mobiles">
          <div>
            <p>
              RSVP on <a href="http://www.meetup.com/nescala/">Meetup</a>
            </p>
            <p>
              <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>Day one</a> |
              <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>Day two</a>
            </p>
          </div>
        </div>
        <div class="left unit half center-on-mobiles">
          { session match {
              case Some(member) if member.nescalaMember =>
                <p class="pushdown">
                {
                  if (Site.proposalsOpen)
                    <span><a href="/2016/talks#speak" class="btn">Propose a talk</a> (by Mon Dec 8)</span>
                  else if (Site.votesOpen)
                    <span><a href="/2016/talks#proposals" class="btn">Vote for a talk</a> (polls close Mon Dec 15 11:59pm)</span>
                  else
                    pollsClosed
                }
                </p>
              case Some(member) =>
                <p class="pushdown">
                {
                  if (Site.proposalsOpen)
                    <span>Join our <a href="http://www.meetup.com/nescala/">Meetup group</a> to submit a proposal (by Mon Dec 8)</span>
                  else if (Site.votesOpen)
                    <span>RSVP'd our <a href="http://www.meetup.com/nescala/">Meetup group</a> to submit a proposal (by Mon Dec 8)</span>
                  else
                    pollsClosed
                }
                </p>
              case _ =>
                <p class="pushdown">
                {
                  if (Site.proposalsOpen)
                    <span><a href="/login?state=propose" class="btn">Login</a> to submit a talk proposal (by {ProposalCutoffStr})</span>
                  else if (Site.votesOpen)
                    <span>
                      <a href="/login?state=proposals" class="btn">Login</a>
                      to vote on talks (polls close {VotesCutoffStr} 11:59pm)
                    </span>
                  else
                    pollsClosed
                }
                </p>
          }}
        </div>
      </div>
      <div class="communicate">
        <a class="icon" href="http://twitter.com/nescalas" target="_blank"><i class="fa fa-twitter"></i>
          <span>Listen for our call</span>
        </a>
        <a href="http://www.meetup.com/nescala/" target="_blank" class="icon"><i class="icon-scala"></i><span>Join our community</span></a>
        <a href="#what" class="icon"><i class="fa fa-check-circle-o"></i><span>Listen to your peers</span></a>
        <a href="#when" class="icon"><i class="fa fa-calendar-o"></i><span>Mark your calendar</span></a>
        <a href="#where" class="icon"><i class="fa fa-map-marker"></i><span>Align your compass</span></a>
      </div>
    )(
      <div class="inverse" id="what">
        <div class="grid">
          <div class="unit">
            <h2>
              This is <strong>your</strong> conference.
            </h2>
            <p>
              Northeast Scala Symposium is a <a href="http://scala-lang.org/">Scala</a>-focused <strong>community</strong> gathering.
            </p>
            <p>
              A uniquely-blended programming language deserves a uniquely-blended conference format. NE Scala offers a mix of speaker-oriented conference presentations with unconference-style sessions and discussions. All presenters are attendees and all attendees select presenters.
            </p>
            <h2><strong>Day 1 schedule</strong></h2>
          </div>
        </div>
        <div>{
          slots.map {
            case Schedule.Open(at) =>
              <div class="grid">
                <h3 class="right unit one-fifth">
                  { timestamp(at) }
                </h3>
                <h3 class="unit four-fifths">
                  Doors Open
                </h3>
              </div>
                <div class="grid">
                  <div class="unit one-fifth"></div>
                  <div class="unit four-fifths">
                    <p>Check in and grab some coffee and snacks, courtesy of
                      <a href="http://www.example.com"><i>we don't know yet</i></a>.
                    </p>
                  </div>
                </div>
            case Schedule.Intro(at) =>
              <div class="grid">
                <h3 class="right unit one-fifth">
                  { timestamp(at) }
                </h3>
                <h3 class="unit four-fifths">
                  Opening Remarks
                </h3>
              </div>
                <div class="grid">
                  <div class="unit one-fifth"></div>
                  <div class="unit four-fifths">
                    <p>We promise: These will be brief.</p>
                  </div>
                </div>
            case Schedule.Break(at, length) =>
              <div class="grid">
                <h3 class="right unit one-fifth">
                  { timestamp(at) }
                </h3>
                <h3 class="unit four-fifths break">
                  Break ({length} minutes)
                </h3>
              </div>
            case Schedule.Close(at) =>
              <div class="grid">
                <h3 class="right unit one-fifth">
                  { timestamp(at) }
                </h3>
                <h3 class="unit four-fifths">
                  Day one closer
                </h3>
              </div>
            case Schedule.Party(at) =>
              <div class="grid">
                <h3 class="right unit one-fifth">
                  { timestamp(at) }
                </h3>
                <h3 class="unit four-fifths">
                  Chariot Drink-up
                </h3>
              </div>
                <div class="grid">
                  <div class="unit one-fifth"></div>
                  <div class="unit four-fifths">
                    <p>Until 9pm, a few blocks away at
                      <i>TBD</i>.
                      Drinks and appetizers included. Bring your nametag.
                      Thank you <a href="http://www.chariotsolutions.com">Chariot Solutions</a> for
                      sponsoring!</p>
                  </div>
                </div>
            case Schedule.Lunch(at) =>
              <div class="grid">
                <h3 class="right unit one-fifth">
                  { timestamp(at) }
                </h3>
                <h3 class="unit four-fifths">
                  Lunch
                </h3>
              </div>
                <div class="grid">
                  <div class="unit one-fifth"></div>
                  <div class="unit four-fifths">
                    <p>Let's eat!</p>
                  </div>
                </div>
            case slot @ Schedule.Talk(p) =>
              <div class="grid" id ={ s"talk-${p.domId}" }>
                <h3 class="right unit one-fifth">
                  { timestamp(slot.time, true) }
                </h3>
                <h3 class="unit four-fifths">
                  <a href={s"#talk-${p.domId}"}>{ p.name }</a>
                </h3>
              </div>
                <div class="grid">
                  <div class="unit one-fifth right center-on-mobiles">
                    <p>{ personal(p) }</p>
                  </div>
                  <div class="unit four-fifths">
                    <p>{ p.desc }</p>
                    { p.slides.fold(<span/>) { slides =>
                      <a href={slides} target="_blank">
                        <i class="fa fa-film"></i> Slides
                      </a>
                    } }
                    { p.video.fold(<span/>) { video =>
                      <a href={video} target="_blank">
                        <i class="fa fa-film"></i> Video
                      </a>
                    } }
                  </div>
                </div>
          }
        }</div>
        <div class="grid">
          <div class="unit">
            <h2><strong>Day 2 schedule</strong></h2>
            <p>See the <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>Meetup page</a>.</p>
          </div>
        </div>
      </div>
      <div id="when" class="regular">
        <div class="grid">
          <div class="unit">
            <h2>When</h2>
            <p>
              Northeast Scala Symposium is held annually. In 2016, we will
              descend upon the City of Brotherly Love, on
              <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>Friday, March 4</a>
              and <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>Saturday, March 5</a>.
            </p>
            <p>
              To attend, RSVP separately for <a href={s"http://www.meetup.com/nescala/events/$Day1EventId/"}>day one</a> and
              <a href={s"http://www.meetup.com/nescala/events/$Day2EventId/"}>day two</a>.
            </p>
          </div>
        </div>
      </div>
      <div class="inverse" id="where">
        <div class="grid">
          <div class="unit whole">
            <h2>Where</h2>
            <p>
              This year's symposium will be held at
              <a href="http://thehub.com/locations/cira-centre/">The Hub Cira Center</a>
              located at
              <a href="https://www.google.com/maps/place/2929+Arch+St,+Philadelphia,+PA+19104/@40.002498,-75.1180329,11z/data=!4m2!3m1!1s0x89c6c64bdd84625b:0xbe940a019709b223">2929 Arch Street, Philadelphia, Pa 19104</a>,
              right next to historic <a href="https://en.wikipedia.org/wiki/30th_Street_Station">30th Street Station</a>,
              which makes it highly accessible via mass transit.
            </p>
          </div>
        </div>
      </div>
      <div class="regular" id="friends">
        <div class="grid center-on-mobiles">
          <div class="unit whole">
            <h2>Friends</h2>
            <p>Below are some of the sponsors that made this possible.</p>
            <div class="sponsors">{
              sponsors.map { sponsor =>
                <p class="unit one-third">
                  <a class="friend" title={sponsor.name} href={sponsor.link}>
                    <img src={sponsor.image}/>
                  </a>
                </p>
              }
            }</div>
          </div>
        </div>
      </div>)
      <div id="kindness" class="inverse">
        <div class="grid">
          <div class="unit whole">
            <h2>Code of Conduct.</h2>
            <p>
              Simply: Be kind.
            </p>
            <p>
              Nobody likes a jerk, so <strong>show respect</strong> for those around you.
            </p>
            <p>
              NE Scala is dedicated to providing a harassment-free experience for everyone, regardless of gender, gender identity and expression, sexual orientation, disability, physical appearance, body size, race, or religion (or lack thereof). We do not tolerate harassment of participants in any form.
            </p>
            <p>
              All communication should be appropriate for a technical audience including people of many different backgrounds. Sexual language, innuendo, and imagery is not appropriate for any symposium venue, including talks.
            </p>
            <p>
              Participants violating these rules may be asked to leave without a refund at the sole discretion of the organizers.
            </p>
            <p>
              Since this is a gathering of static typists, offenders will be caught at compile time.
            </p>
          </div>
        </div>
      </div>

  def layout
   (session: Option[SessionCookie] = None)
   (scripts: Iterable[String] = Nil,
    styles: Iterable[String] = Nil)
   (headContent: xml.NodeSeq)
   (bodyContent: xml.NodeSeq) = Html5(
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <meta name="viewport" content="width=device-width,initial-scale=1"/>
        <title>&#8663;northeast scala symposium</title>
        <link href="https://fonts.googleapis.com/css?family=Source+Code+Pro|Montserrat:400,700|Open+Sans:300italic,400italic,700italic,400,300,700" rel="stylesheet" type="text/css"/>
        <link href="https://fonts.googleapis.com/css?family=Vollkorn:400,400italic,700,700italic" rel="stylesheet" type="text/css"/>
        <link href="https://fonts.googleapis.com/css?family=Signika:400,700" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="/css/gridism.css" />
        <link rel="stylesheet" type="text/css" href="/css/normalize.css" />
        <link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet"/>
        <link href="/css/font-mfizz/font-mfizz.css" rel="stylesheet"/>
        <link rel="stylesheet" type="text/css" href="/css/2016/style.css" />
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>{
          styles.map { s => <link rel="stylesheet" type="text/css" href={s}/> } ++
          scripts.map { s => <script type="text/javascript" src={s}></script> }
        }
        <script type="text/javascript" src=""></script>
      </head>
      <body class="wrap wider">
        <div class="unit whole center-on-mobiles top section" id="banner">
          <div class="title-block">
            <h1>NE Scala 2016</h1>
            <h2>Philadelphia railyard edition</h2>
          </div>
          { headContent }
          <hr/>
        </div>
        { bodyContent }
        <div class="footer unit whole center-on-mobiles">
          <div class="container inverse">
          <a href="#top">NE Scala</a> is organized with <span class="love">❤</span> from the
          <div>
            <a href="http://www.meetup.com/scala-phase/">Philadelphia</a>,
            <a href="http://www.meetup.com/ny-scala/">New York</a>,
            and <a href="http://www.meetup.com/boston-scala/">Boston</a>
            Scala enthusiasts,
            our <a href="#friends">friends</a> and, of course, of all of
            <a href="http://www.meetup.com/nescala/photos/">you</a>.
          </div>
          <div>
            Hosting by the fine folks @ <a href="https://www.heroku.com/">Heroku</a>
          </div>
          <div>
            &nbsp;
          </div>
          <div>
            previous years:
              <a href="/2015">2015</a> |
              <a href="/2014">2014</a> |
              <a href="/2013">2013</a> |
              <a href="/2012">2012</a> |
              <a href="/2011">2011</a>
          </div>
          </div>
        </div>
      </body>
    </html>
  )
}
