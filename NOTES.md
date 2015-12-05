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