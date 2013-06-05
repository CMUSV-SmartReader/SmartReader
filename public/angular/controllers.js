function ArticleCtrl($scope, $http){
	var getCategories = function(){
		$http.jsonp({'url': "/categories"}).success(function(data, status){
			console.log(data);
		}).error(function(data, status){
			console.log("error: " + data);
		});
	};
	var getFeeds = function(categoryId){
		var url = "/feeds";
		if(categoryId !== undefined) {
			url = "/category/" + categoryId ; 
		}
		$http.jsonp({'url': url}).success(function(data, status){
			console.log(data);
		}).error(function(data, status){
			console.log("error: " + data);
		});
	}
	var getArticles =function(categoryId){
		var url = "/articles";
		if(categoryid !== undefined){
			var url = "/cateogry/" + categoryId + "/articles";
		}
		$http.jsonp({'url': url}).success(function(data, status){
			console.log(data);
		}).error(function(data, status){
			console.log("error: " + data);
		});
	}
	
}