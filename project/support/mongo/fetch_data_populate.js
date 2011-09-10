cities = [
	"Atlanta",
	"Austin",
	"Boston",
	"Baltimore",
	"Canada",
	"Charlotte",
	"Chicago",
	"Dallas",
	"Denver",
	"Detroit",
	"Houston",
	"Kansas-City",
	"Los-Angeles",
	"Minneapolis",
	"New-York",
	"Orlando",
	"Philadelphia",
	"Phoenix",
	"Portland",
	"Raleigh"
];

cities.forEach(function(city) {
	c = { "fetch_group" : "dice", "location" : city, "url" : "http://rss.dice.com//system/"+city.toLowerCase()+"-jobs.xml", "fetch_type" : "rss"};
	db.fetch_data.save(c);
}
