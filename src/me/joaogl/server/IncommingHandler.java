/**
 * This file is part of ServerSideAcars.

    ServerSideAcars is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ServerSideAcars is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ServerSideAcars.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.joaogl.server;

import me.joaogl.server.aircraftmanager.AircraftManager;
import me.joaogl.server.data.DataManager;
import me.joaogl.server.data.ServerLogger;

public class IncommingHandler {
	int stage = 0;

	public String processInput(String in, int ID) {
		String out = "Empty command";
		if (in != null) {
			out = "Unknown command - " + in;
			if (in.equalsIgnoreCase("start") && stage == 0) {
				stage = 1;
				return "Starting a new flight, tell me the aircraft registration.";
			} else if (in.equalsIgnoreCase("start")) return "You've already started a flight.";
			if (stage == 1) {
				if (check(in)) return cancel();
				else {
					if (isInt(in)) {
						if (AircraftManager.getReg(Integer.parseInt(in)) != null) {
							int id = AircraftManager.getId(AircraftManager.getReg(Integer.parseInt(in)));
							stage = 2;
							return "Thank you, aircraft requested is " + AircraftManager.getReg(id) + " registred with the id " + id + " which fuel available is " + AircraftManager.getFuel(id) + " and aircraft life is " + AircraftManager.getLife(id) + ".";
						} else return "Type list to get all Planes registrations";
					} else {
						if (AircraftManager.getId(in) != DataManager.getErrorvalue()) {
							int id = AircraftManager.getId(in);
							stage = 2;
							return "Thank you, aircraft requested is " + AircraftManager.getReg(id) + " registred with the id " + id + " which fuel available is " + AircraftManager.getFuel(id) + " and aircraft life is " + AircraftManager.getLife(id) + ".";
						} else return "Type list to get all Planes registrations, " + AircraftManager.getId(in);
					}
				}
			}
			if (stage == 2) if (check(in)) return cancel();
			if (in.equalsIgnoreCase("Disconnecting") || in.equalsIgnoreCase("disc")) return "disc";
			if (in.equalsIgnoreCase("list")) {
				String result = "Aircraft List sent to the server console.";
				for (int i = 0; i < AircraftManager.aircraftReg.size(); i++)
					ServerLogger.println(AircraftManager.getAllPlanes(i));

				return result;
			}
			if (in.contains("delid")) {
				if (in.contains(" ")) {
					String[] args = in.split(" ");
					if (args[1] != null && isInt(args[1])) {
						int id = Integer.parseInt(args[1]);
						if (id <= AircraftManager.getTotal()) {
							AircraftManager.removeId(id);
							return "Removed id " + args[1];
						} else return "Error: invalid ID. Type list";
					} else return "Error: value typed is not an ID. Type delid (id)";
				} else return "Error: value typed is not an ID. Type delid (id)";
			}
			if (in.equalsIgnoreCase("restart")) {
				DataManager.setupRegList();
				return "Restart";
			}
		}
		return out;
	}

	private boolean check(String in) {
		if (in.equalsIgnoreCase("abort") || in.equalsIgnoreCase("cancel") || in.equalsIgnoreCase("end") || in.equalsIgnoreCase("finish") || in.equalsIgnoreCase("stop") || in.equalsIgnoreCase("close")) { return true; }
		return false;
	}

	private String cancel() {
		stage = 0;
		return "Flight cancelled.";
	}

	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}