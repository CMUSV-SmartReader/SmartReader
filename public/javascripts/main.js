$(window).on('load', function(){

  // Put dummy categories
  smartreader.putCategories([
    {text:'category 1', feeds: [new smartreader.feed("Feed1"), new smartreader.feed("Feed2")]},
    {text:'category 2', feeds: [new smartreader.feed("Feed3"), new smartreader.feed("Feed4")]},
    {text:'category 3', feeds: [new smartreader.feed("Feed5")]},
  ]);

  // Put dummy articles
  smartreader.putArticles([
    new smartreader.article( "Title1", "Author1", (new Date()), "Description 1" ),
    new smartreader.article( "Title2", "Author2", (new Date()), "Description 2" ),
    new smartreader.article( "Title3", "Author3", (new Date()), "Description 3" ),
  ]);
});
