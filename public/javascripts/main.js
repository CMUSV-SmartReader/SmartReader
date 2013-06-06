$(window).on('load', function(){

  // Put dummy categories
//  smartreader.putCategories([
//    {text:'category 1', feeds: [new smartreader.feed("1", "Feed1"), new smartreader.feed("2", "Feed2")]},
//    {text:'category 2', feeds: [new smartreader.feed("3", "Feed3")]},
//    {text:'category 3', feeds: [new smartreader.feed("4", "Feed4"), new smartreader.feed("5", "Feed5")]},
//  ]);

  smartreader.loadCategories();


  // Put dummy articles
  smartreader.putTitle("Technology");
  smartreader.putArticles([
    new smartreader.article( "Title1", "Author1", (new Date()), "Feed1", "Description 1" ),
    new smartreader.article( "Title2", "Author2", (new Date()), "Feed2", "Description 2" ),
    new smartreader.article( "Title3", "Author3", (new Date()), "Feed3", "Description 3" ),
  ]);
});
