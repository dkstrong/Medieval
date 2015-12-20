things to expiremnt with:

no confirmation - would this even work? 
widely different framerates
compare both clients Scenarios, either with a huge "dump" file and compare everything, or with an md5 hash or something
	have skeletons attack eachother/etc to create more varying gamestates, and see if health values and whatnot stay in sync.
various "frameLength"
various "gameFramesPerLocksetpTurn"
	figure out how to incorporate some kind o ping/framerate measurement into finding the optimal values for these..

maybe having the server incrementing the lockstemp frame by either 1 or 3 depending on lag

having the server increment the lockstep frame more for laggy players. (would be sort of complicated i think)





Client						Server							Other Client
sends player.Id/lockstepFrame		

	
						increments locktepFrame+2, 					
						transmits to all clients EXCEPT the sender
						stores incremented action
													stores source clients incremented action
													confirms receipt of incremented action

						
						server receives confirmation
						retreives the stored incremented action and 
						gives it to sender

client stores their own incrmeented action
because it sees that it is its own action
it doesnt trigger the confirmation code




Graphics todo:

frustum culling for infantry and structures. use their collision boxes as bounding boxes...












== Mining and Resources

Gold - collected through central building based on time (no worker needed)
Food - collected from farm structures to stockpile structure
Wood - collected from tree mine, taken to lumber structure, taken to stockpile structure
Stone - collected from stone mine, tak


== Population

number of houses represent max population (workers)
soldiers are purchased at barracks and require a build time, soldiers slowly drain food and gold




Worker Paths:
// TODO: need to incorporate if the worker is interrupted on its task and then resumes it, (eg if the worker is attacked but survives)
// Or if the building mode changes, or if repair is activated etc etc
// TODO: need Unassign work to account for workers that are unassigned due to low popularity or died

Structure
	Build
		1. Take Idle Worker/Use Assigned Worker 2. Worker Moves to structure 3. Worker builds structure 4. [Operate Structure]
	Repair
		1. Take Idle Worker/Use Assigned Worker 2. Worker Moves to structure 3. Worker repairs structure 4. [Operate Structure]
	Operate Structure (Requires Build, if not build then does build instead)
		1. [Unassign Worker]
	Unassign Worker
		1. Assign Worker to [Operate Structure] on closest idle building 2. if no idle building exists then assigned to keep
		
Granary 
	
Farm
	Operate Structure
		1. Take Idle Worker/Use Assigned Worker. 
		2. Worker Moves to Farm.
		3. Worker collects Food. 
		4. Worker carries Food to Granary. 
		5. Worker places food.
		6. [Operate Structure]

Lumber Camp
	Operate Structure
		1. Take Idle Worker/Use Assigned Worker.
		2. Attach Tree to lumber camp if there is none/Use Already Attached Tree 
		3. Worker Moves to attached tree
		4. Worker Collects Wood Log from tree
		5. Worker carries Wood Log to lumber camper
		6. Worker converts Wood Log to Wood
		7. Worker carries Wood to Stockpile
	 	8. Worker places Wood
		9. [Operate Structure]
	Unassign Worker
			1. [super.Unassign Worker]
			2. Unattach tree so that other lumber camps to attach to it
			
				
Stone Mine
	Operate Structure
		1. Take Idle Worker/Use Assigned Worker.
		2. Worker moves to Stone Mine
		3. worker spawns stone in mine
		4. Attach closest unattached ox tether to this mine/ Do nothing if already have attached ox tether 
		5. [Operate Structure]	
		
Ox Tether
	Operate Structure
		1. Take Idle Worker/Use Assigned Worker.
			a. If attached
				2. Worker moves to mine
				3. Collect Stone
				4. if cart full then detaach from mine
				5. [Operate Structure]
			b. If not attached
				c.If cart contains stone
					6. Worker moves to stockpile
					7. Worker places stone in stockpile
					8. [Operate Structure]
				d.If cart empty
					9. Worker moves to ox post
					



















