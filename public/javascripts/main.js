$(window).on('load', function(){

  // Put dummy categories
  /*smartreader.putCategories([
    {text:'category 1', feeds: [new smartreader.feed("1", "Feed1"), new smartreader.feed("2", "Feed2")]},
    {text:'category 2', feeds: [new smartreader.feed("3", "Feed3")]},
    {text:'category 3', feeds: [new smartreader.feed("4", "Feed4"), new smartreader.feed("5", "Feed5")]},
  ]);*/

  $.ajax({
    type: "POST",
    url: "/import",
    success: function(){
      console.log("try import");
    }
  });

  smartreader.loadCategories();
  smartreader.putTitle("Home");
  $('#menu .collapse-inner').perfectScrollbar();


  $('#menu').on('click', 'li', function(){
    var feedid = $(this).attr("data-feedid");
    if(feedid){
      $.ajax({
        dataType: "json",
        url: "/feed/"+feedid,
        success: function(data){
          console.log(data);
        }
      });
    }
  });

  // Put dummy articles
  //smartreader.putTitle("Technology");
  smartreader.putArticles([
    new smartreader.article( "Humble Mumble", "Humble Mumble", "Monday", "Humble Bundle", "Humble Indie Bundle 8 is now 11 games strong! We just added four stupendous games to the Humble Indie Bundle 8! If you’ve already purchased a bundle, you’ll find *Tiny & Big in Grandpa’s Leftovers*, *English Country Tune*, *Intrusion 2*, and *Oil Rush* available now on your download page! These ... See more from software" ),
    new smartreader.article( "Making Google’s CalDAV and CardDAV APIs available for everyone", "Scott Knaster", "9:15AM", "Google Code Blog", "In March we announced that CalDAV, an open standard for accessing calendar data across the web, would become a partner-only API because it appeared that almost all the API usage was driven by a few large developers. Since that announcement, we received many requests for access to CalDAV, giving us a better understanding of developers’ use cases and causing us to revisit that decision. In response " ),
    new smartreader.article( "Morphing Devices", "Mary Lou", "May 15", "Feed3", "Today we want to share an experimental “morphing” slideshow with you. The idea is to transition between different devices that show a screenshot of a responsive website or app by applying a “device class”. By using the same elements and pseudo-elements for all the devices, we can create an interesting morph effect. We will control the classes and the switching of the image with some Java" ),
  ]);
});
