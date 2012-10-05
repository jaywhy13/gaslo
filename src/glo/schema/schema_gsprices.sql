-- Holds the gas station prices 
CREATE TABLE GSPrices (
	gs_id integer,
	gst_id integer,
	price double,
	foreign key (gs_id) references GStation (id) on update cascade on delete cascade,
	foreign key (gst_id) references GSType (id) on update cascade on delete cascade	
);
