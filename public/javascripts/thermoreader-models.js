var thermoreader = thermoreader || {};
thermoreader.model = {};

thermoreader.model.feed = function(id, name, updated, articles) {
  this.id = id || "";
  this.name = name || "";
  this.updated = updated || new Date();
  this.articles = articles || [];
};

thermoreader.model.article = function(title, author, date, feedName, summary, des, link, popular) {
  this.title = title || "";
  this.author = author || "";
  this.date = date || {};
  this.feedName = feedName || "";
  this.summary = summary || ""
  this.description = des || "";
  this.link = link || "";
  this.popular = popular || 1;
}