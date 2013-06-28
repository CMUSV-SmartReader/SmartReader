var thermoreader = thermoreader || {};
thermoreader.model = {};

thermoreader.model.feed = function(id, name, userFeedId, updated, articles) {
  this.id = id || "";
  this.name = name || "";
  this.userFeedId = userFeedId || "";
  this.updated = updated || new Date();
  this.articles = articles || [];
};

thermoreader.model.article = function(id, title, author, date, feedName, summary, des, link, popular, read) {
  this.id = id || "";
  this.title = title || "";
  this.author = author || "";
  this.date = new Date(date) || {};
  this.feedName = feedName || "";
  this.summary = summary || ""
  this.description = des || "";
  this.link = link || "";
  this.popular = popular || 1;
  this.read = read || false;
  this.expanded = false;
}
