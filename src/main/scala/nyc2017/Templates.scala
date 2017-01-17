package nescala.nyc2017

import nescala.{ Meetup, SessionCookie, Util }
import unfiltered.response.{Html5, ResponseFunction}
import scala.xml.{NodeSeq, Elem}

trait Templates {

  def meetupLogo =
    <svg class="meetup-logo" viewBox="199 58 56 55" xmlns="http://www.w3.org/2000/svg">
      <g fill="none" fillRule="evenodd">
        <path d="M209.4884518 102.4100214c.0029285-.5982906-.5022321-1.1143163-1.0981752-1.1202992-.60326424-.0044872-1.09963944.5130342-1.09524674 1.1442308.0043927.6162393.49491097 1.1038461 1.10403215 1.099359.59887156-.0029915 1.0879256-.5070513 1.0893898-1.1232906m43.7351939-22.2803419c-.576908-.0074786-1.1011037.5205128-1.1084248 1.1188034-.0073212.6192308.4963752 1.1337607 1.1113533 1.1352564.6105854.0014957 1.0893898-.4831196 1.0937825-1.109829.0058569-.6297009-.4802686-1.1367522-1.096711-1.1442308m-38.1022872-21.48910258c-.708689-.00299145-1.2548482.55042735-1.2548482 1.2698718 0 .716453.5520161 1.27286325 1.2607052 1.2698718.6867255-.0044872 1.2240993-.5638889 1.2240993-1.27435898 0-.716453-.5285884-1.26089744-1.2299563-1.26538462m35.2982798 28.30961538c-.8038642-.0059829-1.5008394.7059829-1.4935182 1.5271368.0058569.8106837.6691547 1.4747863 1.4700905 1.476282.8419343.0014957 1.4715548-.6311966 1.4744833-1.4792735.0014642-.8391026-.6442628-1.5181624-1.4510556-1.5241453m-23.6005174 24.007906c.0087854-.8450855-.6720832-1.5405983-1.5081606-1.5435898-.8595051-.0029914-1.5330526.6775641-1.5315884 1.5420941.0029285.8540598.6735475 1.5301282 1.516946 1.5301282.8433986.0014957 1.5154818-.673077 1.522803-1.5286325m-18.48302094-41.841453c0 1.0141026.77311534 1.8113248 1.76000884 1.8128205.9986073.0014957 1.7731869-.8002137 1.7702584-1.8292735-.0029285-.9976496-.777508-1.7784188-1.761473-1.77542734-1.0103212.00149572-1.76879424.77029914-1.76879424 1.79188034m40.59441284 5.7690171c-.0043927-1.0814103-.9063607-1.9997863-1.96793-2.007265-1.0835329-.0074786-2.0016074.9273505-1.9957504 2.0326923.0087854 1.1023505.8829328 1.9863248 1.9693942 1.9878206 1.1172102.0014957 2.0016073-.8899573 1.9942862-2.0132479m-18.947183-14.74935898c-1.2870614-.00747863-2.3354527 1.0440171-2.3427738 2.34679487-.0073212 1.30128204 1.0366774 2.3707265 2.3178818 2.3722222 1.2753475 0 2.300311-1.04401708 2.3017752-2.34679486.0029285-1.3102564-1.0103212-2.36623932-2.2768832-2.37222222M201.31070707 86.7991239c1.28120442.0029915 2.35741612-1.0903846 2.35302342-2.3856837-.00585694-1.2982906-1.06010515-2.3752137-2.3295957-2.3797009-1.30170368-.0029914-2.3354526 1.0514957-2.33398838 2.3826923 0 1.3177351 1.03082047 2.3811966 2.31056065 2.3826923m41.04393253 20.6155983c.0014642-1.3446581-1.1025679-2.4694444-2.4291636-2.4739316-1.3617373-.0044872-2.4643052 1.1083333-2.4701621 2.4873932-.0058569 1.3491453 1.115746 2.493376 2.4438059 2.4948718 1.3456307.0014957 2.4525913-1.1292735 2.4555198-2.5083334m4.3531665-14.1600427c.0043927 4.8985043-3.4497344 9.1194444-8.1455316 9.9480769-.5900861.1047009-1.1874934.1660257-1.7878292.1600427-.2020643-.0029914-.2811329.0792735-.3455592.2692308-.9371095 2.7491453-2.8084 4.3884616-5.6343709 4.8012821-1.8698263.2737179-3.5668731-.2378205-5.0530702-1.432906-.2079212-.1660257-.3338453-.1690171-.5505519-.0104701-1.7395095 1.2698718-3.6898687 1.8337607-5.8188644 1.7021367-4.1569592-.2572649-7.6535491-3.5014957-8.3080615-7.6850427-.0453912-.2901709-.0922467-.5818376-.0966394-.8720085-.0043927-.2438035-.0951752-.3365385-.3206672-.3814103-1.2592409-.251282-2.40134312-.7807692-3.40873585-1.5854701-1.85371976-1.4837607-2.9665373-3.4192307-3.22424242-5.8138889-.2957752-2.7416666.53298103-5.1108974 2.42037817-7.0852564.1522803-.158547.1610657-.2692307.05856934-.4561966-.72625987-1.2938034-1.07035477-2.6952991-1.0147139-4.1820512.1391022-3.7094017 2.66636942-6.7726496 6.18053006-7.5324787.407057-.0882478.6208351-.251282.7994716-.6446581 1.7336526-3.8155983 4.644549-6.13547007 8.6829053-6.90128204 2.8362206-.5369658 5.5113754.0388889 7.9932514 1.5301282.2093854.12564103.3836292.13611112.6091211.06880342 3.9417169-1.1681624 7.4353783-.31858974 10.4019157 2.56217952 1.7526876 1.7021367 2.7146891 3.8574786 2.9987504 6.316453.0614978.5384615.0907825 1.0799145.0512482 1.6213675-.0161066.2034188.0453912.2901709.2357416.3604701 1.7775796.6730769 3.0016789 1.9264957 3.5390526 3.7797008.6676906 2.3049146.1054249 4.3271368-1.579908 6.0083334-.1464234.1465812-.1156745.234829-.0322132.3814102.8902541 1.5690171 1.3514877 3.2576923 1.3500234 5.0735043" fill="#F8093D"/>
      <path d="M229.1165035 92.4694231c.0014642 2.083547 1.3119533 3.926282 3.1686015 4.5514957.9312526.3141026 1.8917898.4636752 2.8655052.5070513.695511.0299145 1.3910219-.0194445 2.0338205-.3470086.5051606-.2572649.7438307-.683547.7204029-1.2504273-.0219635-.5713675-.2723474-.9991453-.8082569-1.225-.1844935-.0792735-.3733796-.1435898-.5710511-.1794872-.5900862-.1061966-1.1831008-.1974359-1.7702585-.3155983-.9678584-.1974359-1.3749154-.7209402-1.3866292-1.7215812-.0117139-.8854701.2225635-1.7200854.4905182-2.5457265.4875898-1.5121795 1.1450307-2.9540598 1.7746512-4.4064102.6076569-1.4029915 1.2460628-2.7940171 1.6750832-4.2717949.2372059-.8106838.3499519-1.624359.1347095-2.4634615-.3265241-1.274359-1.0747474-2.1284188-2.3618088-2.373718-1.1801723-.224359-2.3544876-.2497863-3.4204497.4696581-.3558088.2378205-.6969752.1809829-1.0044643-.0972222-.2357416-.2108974-.4583051-.4337607-.689654-.650641-1.0879256-1.0260684-2.525803-1.0769231-3.6942614-.1391026-.4714833.3784188-.8682906.8465812-1.3793081 1.175641-.4583051.2931624-.9195387.3769231-1.4261635.1002137-.4831971-.2632478-.9825008-.4935897-1.4803402-.7254273-.5051606-.2318376-.9942147-.5205129-1.5623373-.5698718-1.7980789-.1555556-3.6781548.934829-4.4717695 2.5935897-.351416.7344017-.6384058 1.4972222-.9122175 2.2645299-1.2563124 3.5478633-2.281276 7.1720086-3.3413811 10.7826923-.4744117 1.6168804.0849255 3.2068376 1.4246993 4.0803419 1.0454628.6805556 2.1904935.8480769 3.3750585.4846154.9605372-.2946581 1.4861971-1.0844017 1.8537197-1.9713675 1.2211709-2.9405983 2.3208103-5.9320513 3.484876-8.8980769.319203-.815171.6296205-1.6348291.9693227-2.4425214.3440949-.8151709 1.3295241-1.0918803 1.9137533-.5519231.357273.3305556.450984.7688034.4173066 1.2384616-.0395343.5025641-.2298847.9647435-.4085212 1.4254273-.7570087 1.9564103-1.5286599 3.9068376-2.2885971 5.8602564-.1537445.3963675-.3206672.7882479-.3880219 1.2115385-.1171387.7284188.1654584 1.3745726.7482234 1.6318376.595943.2617521 1.2123854.3051282 1.830292.0777778.7174745-.2662394 1.1464949-.8405983 1.4774117-1.5032052 1.1347811-2.2779914 2.2622409-4.5619658 3.3955578-6.8399572.5241956-1.0514958 1.0586409-2.0985043 1.5930861-3.1455129.1991358-.3873931.431949-.7553418.8038643-.9961538.3353095-.2168803.6969752-.2318376 1.0410701-.0358974.3353095.1929487.3587372.5429487.3440949.8899572-.0073212.1854701-.0585693.3679487-.1288526.5399573-.1478876.3604701-.2840613.7254273-.4480555 1.0784188-.9429664 2.0282051-1.9005752 4.0489316-2.8362205 6.0816239-.395343.8570513-.8053285 1.7185898-.7570087 2.6220086" fill="#FFF"/>
      </g>
    </svg>

  def indexPage
   ()
   (session: Option[SessionCookie] = None) =
    layout(session)(scripts = Seq.empty[String])(
      Nil // head content
    )(
      <div class="jumbotron">
        <div class="jumbotron-overlay"></div>
        <div class="container">
          <div class="row">
            <div class="col-12 center">
              <h1>northeast scala symposium</h1>
              <h2 class="font-light">
                a community-driven <a href="https://www.scala-lang.org/">Scala</a> conference
              </h2>
              <h3>back to the big apple this March 23<sup>rd</sup> through 25<sup>th</sup> 2017</h3>
              <a href="https://twitter.com/nescalas" target="_blank">
                <button><i class="twttr-blue fa fa-twitter"></i> stay tuned</button>
              </a>
            </div>
           </div>
        </div>
      </div>
      <div class="body-content">
        <div class="container">
          <div class="row">
            <div class="col-2 hidden-sm"></div>
            <div class="col-8">
              <p>Since 2011, the <a href="https://www.meetup.com/ny-scala/">New York</a>, <a href="https://www.meetup.com/boston-scala/">Boston</a> and <a href="https://www.meetup.com/scala-phase/">Philadelphia</a> Scala meetups have come together every year to host a community driven, Scala conference. The conference rotates between each city, and this year it's in New York City.</p>
              <p>Many of our past speakers have been contributors to <a href="http://typelevel.org/">Typelevel</a> projects. In fact some Typelevel projects began at past Northeast Scala Symposiums! Last year Typelevel colocated their Typelevel Summit along with our conference in Philadelphia, and we're happy to say that it will happen again this year! And to make things simpler, you will only have to purchase one ticket for entrance to both events.</p>
            </div>
         </div>

         <div class="row">
           <div class="col-2 hidden-sm"></div>
           <div class="col-8">
              <h3 class="center m-bottom" id="schedule">Schedule</h3>
              <p class="center">March 23<sup>rd</sup>: <a href="http://typelevel.org/event/2017-03-summit-nyc/">Typelevel Summit</a></p>
              <p class="center">March 24<sup>th</sup>: Northeast Scala Symposium</p>
              <p class="center">March 25<sup>th</sup>: Unconference</p>
           </div>
         </div>

         <div class="row">
           <div class="col-2 hidden-sm"></div>
           <div class="col-8">
              <h3 class="center m-bottom" id="tickets">Tickets</h3>
              <p>Tickets will go on sale January 19th at 12pm EST at <a href="https://ti.to/northeast-scala-symposium/northeast-scala-symposium-2017">ti.to</a> Make sure you're at your computer, because they go fast!</p>
           </div>
         </div>

         <div class="row">
           <div class="col-2 hidden-sm"></div>
           <div class="col-8">

              <h3 class="center m-bottom" id="cfs">Call for Speakers</h3>
              <p>The Northeast Scala Symposium call for speakers is officially open we need <a href="https://www.papercall.io/nescala-nyc-2017">you to participate</a>. The speakers will be chosen by you but we need you to speak up first! Each speaker's proposed talk will be voted on by everyone who buys a ticket, and the talks with the most votes will be the ones you see!</p>
              <p>Typelevel's <a href="http://typelevel.org/event/2017-03-summit-nyc/">call for speakers</a> is already underway and ends January 23rd. Typelevel has a committee that selects speakers, and the speakers they choose will all speak on March 23rd.</p>
              <p>This year we will be accepting proposals through <a href="https://www.papercall.io/">PaperCall</a>. Submit your proposal <a href="https://www.papercall.io/cfps/307/submissions/new">here</a>. We will be closing this submission for on Feb, 15. Afterwards, we will be open up the community poll.</p>
              <p>Need inspiration? See what others voted for <a href="/2016">in</a> <a href="/2015">recent</a> <a href="/2014">years</a>.</p>

           </div>
         </div>

         <div class="row">
           <div class="col-2 hidden-sm"></div>
           <div class="col-8">
              <h3 class="center m-bottom" id="location">Location</h3>
              <p><i>Just a heads up on the location: the Google Street View makes the place look like a dump. The image is from several years ago and has since been renovated and it's much nicer now.</i></p>
              <p>The conference will be in the Dumbo neighborhood of Brooklyn, NY at <a href="http://www.26bridge.com/">26 Bridge</a>, whose address is unsurprisingly: <br/><br/><a href="https://www.google.com/maps/place/26+Bridge+St,+Brooklyn,+NY+11201/@40.7039628,-73.9871895,17z/data=!3m1!4b1!4m5!3m4!1s0x89c25a32e4bc73a7:0xc9dc9bc26ed594d6!8m2!3d40.7039628!4d-73.9850008">26 Bridge St.<br/>Brooklyn, NY 11201</a></p>
              <p>It's just a short, five minute walk from the <a href="https://www.google.com/maps/place/York+Street/@40.7030518,-73.9879941,17z/data=!4m13!1m7!3m6!1s0x89c25a32e4bc73a7:0xc9dc9bc26ed594d6!2s26+Bridge+St,+Brooklyn,+NY+11201!3b1!8m2!3d40.7039628!4d-73.9850008!3m4!1s0x89c25a3389143497:0x673b92bb04377cd9!8m2!3d40.7013507!4d-73.9866026">York St. F-train stop</a>, which is one stop outside of Manhattan. If you are unfamiliar with Dumbo, it's right across the Brooklyn Bridge from Manhattan's Financial District. It's very safe and very trendy. You ought to check out the neighborhood while you're there.</p>



           </div>
         </div>

         <div class="row">
           <div class="col-2 hidden-sm"></div>
           <div class="col-8">
              <h3 class="center m-bottom">Where to stay</h3>
              <p>There is a <a href="https://www.google.com/maps/place/New+York+Marriott+at+The+Brooklyn+Bridge/@40.6974761,-73.9896916,15.67z/data=!4m8!1m2!2m1!1smarriott!3m4!1s0x0:0x175dbb4c26c2225f!8m2!3d40.693669!4d-73.9880034">Marriott</a> not far from the venue. Aside from that there aren't any other hotels in the neighborhood, but it's only a five minute walk from the <a href="https://www.google.com/maps/place/York+Street/@40.6974761,-73.9896916,15.67z/data=!4m8!1m2!2m1!1smarriott!3m4!1s0x89c25a3389143497:0x673b92bb04377cd9!8m2!3d40.7013497!4d-73.9866033">York St. F-train subway stop</a>, so our advice is to find a hotel not far from an F-Train stop so you can take it to the venue. The <a href="https://www.google.com/maps/place/High+Street/@40.6985476,-73.9860459,16z/data=!4m5!3m4!1s0x89c25a36f57f83e7:0xe6e14ba9ee8d83a6!8m2!3d40.6994301!4d-73.9914098">High St. A and C train stop</a> isn't far from the venue either, so staying near one of those trains, or somewhere in the Financial District in lower Manhattan would be a good option. That said, the walk to the venue from the High St. stop is a bit confusing. If you choose this option, make sure your phone is fully charged so you can use Google Maps to find the venue. And honestly Google Maps won't steer you wrong, so if you're comfortable riding the subway, you can stay just about anywhere. Taxis and Uber are obviously an option to get to and from the venue too.</p>
              <p>If you're looking to save a little bit of money, you may be able to find a better deal on Airbnb in Dumbo or a nearby neighborhood. Dumbo and the neighborhoods around Dumbo are safe. Randy and Dustin both live not far from Dumbo, so if you've found an Airbnb you want a sanity check on, please feel free to contact us and we'll be happy to take a look for you.</p>
           </div>
         </div>

         <div class="row">
           <div class="col-2 hidden-sm"></div>
           <div class="col-8">
             <h3 class="center m-bottom" id="codeofconduct">Higher Kindliness</h3>
             <p>
                Nobody likes a jerk. Show respect for those around you.
             </p>
             <p>
                NE Scala is dedicated to providing a harassment-free experience for everyone, regardless of gender, gender identity and expression, sexual orientation, disability, physical appearance, body size, race, or religion (or lack thereof). We do not tolerate harassment of participants in any form.
                All communication should be appropriate for a technical audience, including people of many different backgrounds. Sexual language, innuendo, and imagery is not appropriate for any symposium venue, including talks.
             </p>
              <p>
                Participants violating these rules may be asked to leave without a refund, at the sole discretion of the organizers.
              </p>
              <p>
              Since this is a gathering of static typists, offenders will be caught at compile time.
             </p>
           </div>
         </div>
        </div>
      </div>
    )

  def layout
   (session: Option[SessionCookie] = None)
   (scripts: Iterable[String] = Nil,
    styles: Iterable[String] = Nil)
   (headContent: xml.NodeSeq)
   (bodyContent: xml.NodeSeq) = Html5(
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta name="viewport" content="width=device-width,initial-scale=1"/>
        <link rel="stylesheet" type="text/css" href="/css/normalize.css" />
        <link href="/css/font-mfizz/font-mfizz.css" rel="stylesheet"/>
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet"/>
        <link rel="stylesheet" type="text/css" href="/css/2017/simple-grid.min.css"/>
        <link rel="stylesheet" type="text/css" href="/css/2017/nyc.css"/>
        <title>&#8663;northeast scala symposium</title>
      </head>
      <body>
       { bodyContent }
        <footer>
          <div class="container">
            <div class="row">
              <div class="col-12 center">
                <p>
                  NE Scala is made possible with ❤ from the
                  <a href="http://www.meetup.com/boston-scala/">Boston</a>,
                  <a href="http://www.meetup.com/scala-phase/">Philadelphia</a>,
                  and <a href="http://www.meetup.com/ny-scala/">New York</a> scala enthusiasts,
                  and of course, of all of <a href="http://www.meetup.com/nescala/photos/">you</a>
               </p>
               <p>
                  Hosting by the fine folks @ <a href="https://heroku.com">Heroku</a>
                </p>
              </div>
              <div class="col-12 center">
                <p>
                   {{
                    <a href="/2016">2016</a>,
                    <a href="/2015">2015</a>,
                    <a href="/2014">2014</a>,
                    <a href="/2013">2013</a>,
                    <a href="/2012">2012</a>,
                    <a href="/2011">2011</a>
                  }}
                </p>
                </div>
              </div>
            </div>
        </footer>
      </body>
    </html>
  )
}
