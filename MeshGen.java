package generators;
import java.io.IOException;
import java.util.ArrayList;
import math.Vector2;
import math.Vector3;
import meshGen.OBJFace;
import meshGen.OBJMesh;
import meshGen.OBJMesh.OBJFileFormatException;


public class MeshGen{

	public ArrayList<Vector3> v;
	public ArrayList<Vector2> vt;
	public ArrayList<Vector3> vn;
	public ArrayList<OBJFace> f;


	public static OBJMesh genCylinder (int numV){
		OBJMesh m = new OBJMesh();
		return genCF(numV,genCVN(numV,genCVT(numV,genCV(numV,m))));
	}

	//generate vertices
	public static OBJMesh genCV(int numV, OBJMesh m){
		double theta = Math.PI*2/numV;
		//0-(n-1) vertices on bottom ring
		for (int i = 0; i< numV; i ++){
			float posX = (float)Math.cos(i*theta - Math.PI/2);
			float posZ = (float)Math.sin(i*theta - Math.PI/2);
			Vector3 vBot = new Vector3(posX,-1,posZ);
			m.positions.add(vBot);
		}
		//n-(2n-1) vertices on top ring
		for (int i = 0; i< numV; i ++){
			float posX = (float)Math.cos(i*theta - Math.PI/2);
			float posZ = (float)Math.sin(i*theta - Math.PI/2);
			Vector3 vTop = new Vector3(posX,1,posZ);
			m.positions.add(vTop);
		}
		//2n top origin, 2n+1 bottom origin
		m.positions.add(new Vector3(0,1,0)); //top origin
		m.positions.add(new Vector3(0,-1,0)); //bottom origin
		//# of vertices = 2+2*numV
		return m;
	}

	//generating textures for the sphere
	public static OBJMesh genCVT(int numV, OBJMesh m){
		//side bottom
		for (int i = numV; i>=0; i --){
			//loop for vectors on the side (including u meets v)
			float division = (float) i/numV;
			Vector2 sideBot = new Vector2(division, (float)0);
			m.uvs.add(sideBot);
		}
		
		//side top
		for (int i = numV; i>=0; i --){
			//loop for vectors on the side (including u meets v)
			float division = (float) i/numV;
			Vector2 sideTop = new Vector2(division, (float)0.5);
			m.uvs.add(sideTop);
		}

		//now # of vertices in uvvert = 2*(numV+1)+2

		double theta = Math.PI*2/numV;

		for(int i = 0; i < numV; i++){
			//bottom cap
			float posXB = (float) (Math.cos(i*theta - Math.PI/2)*0.25 + 0.25);
			float posZB = (float) (Math.sin(i*theta - Math.PI/2)*0.25 + 0.75);
			Vector2 capBot = new Vector2(posXB, posZB);
			m.uvs.add(capBot);
		}
		
		for (int i = 0; i<numV; i++){
			//top cap
			float posXT = (float) (Math.cos(-i*theta + Math.PI/2)*0.25 + 0.75);
			float posZT = (float) (Math.sin(-i*theta + Math.PI/2)*0.25 + 0.75);
			Vector2 capTop = new Vector2(posXT, posZT);
			m.uvs.add(capTop);

		}
		
		m.uvs.add(new Vector2((float)0.75, (float)0.75));//upper right top cap center
		m.uvs.add(new Vector2((float)0.25, (float)0.75));//upper left bottom cap center


		//total # of vertices = 2*(numV+1)+2+2*numV=4*numV+4
		return m;
	}

	//generates all the vertex normals in the cylinder
	public static OBJMesh genCVN(int numV,OBJMesh m){
		double theta = Math.PI*2/numV;
		for (int i = 0; i< numV; i ++){
			float posX = (float)Math.cos(i*theta - Math.PI/2);
			float posZ = (float)Math.sin(i*theta - Math.PI/2);
			Vector3 normal = new Vector3(posX,0,posZ);
			m.normals.add(normal);
		}

		m.normals.add(new Vector3(0,1,0)); //top cap & origin
		m.normals.add(new Vector3(0,-1,0)); //bottom cap & origin

		return m;
	}

	public static OBJMesh genCF(int numV,OBJMesh m){

		//side of cylinder
		for(int i = 0; i < numV; i++){
			OBJFace f1 = new OBJFace(3,true,true);
			f1.setVertex(0, i, i, i);
			f1.setVertex(1, i+numV, i+1+numV, i);
			if (i+1 == numV)
				f1.setVertex(2, 0, numV, 0);
			else
				f1.setVertex(2, i+1, i+1, i+1);

			OBJFace f2 = new OBJFace(3,true,true);
			if(i+1 == numV){
				f2.setVertex(0, i+numV, i+1+numV, i);
				f2.setVertex(2, 0, i+1, 0); //debug here
				f2.setVertex(1, numV, i+2+numV, 0); //debug here
			}else{
				f2.setVertex(0, i+numV, i+1+numV, i);
				f2.setVertex(2, i+1, i+1, i+1);
				f2.setVertex(1, i+1+numV, i+2+numV, i+1);
			}

			m.faces.add(f1);
			m.faces.add(f2);
		}

		//skip seam

		//bottom of cylinder
		for (int i = 0;i<numV;i++){
			OBJFace f = new OBJFace(3,true,true);
			f.setVertex(0, 2*numV+1, 4*numV+3, numV+1); //bottom center
			f.setVertex(1, i, i+2*numV+2, numV+1);
			if (i+1 == numV)
				f.setVertex(2, 0, 2*numV+2, numV+1);
			else
				f.setVertex(2, i+1, i+2*numV+3, numV+1);
			m.faces.add(f);
		}

		//top of cylinder
		for (int i = numV; i<numV*2; i++){
			OBJFace f = new OBJFace(3,true,true);
			f.setVertex(0, 2*numV, 4*numV+2, numV); //top center
			f.setVertex(2, i, i+2*numV+2, numV);
			if (i+1 == numV*2)
				f.setVertex(1, numV, 3*numV+2, numV);
			else 
				f.setVertex(1, (i+1), i+2*numV+3, numV);
			m.faces.add(f);
		}
		return m;
	}

	//////////////////////////////
	//Problem 2 Normalization Overall Goal

	//take in a OBJ file containing info with faces and vertices,
	//generate a normal for each vertex by averaging the normal of each face
	//edit all the faces in the mesh to reflect the new normals
	public static OBJMesh normalize(String filename) throws OBJFileFormatException, IOException{
		//parse input into a mesh without normals
		OBJMesh m = new OBJMesh();
		m.parseOBJ(filename);
		m.normals.addAll(genNormals(m));	
		return addVToFace(m);
	}

	//construct new faces that includes vertex normals.
	public static OBJMesh addVToFace(OBJMesh m){		
		for (int i = 0; i < m.faces.size();i++){
			OBJFace f = new OBJFace(3,false,true);
			OBJFace o = m.faces.get(i);
			for (int j = 0; j<3;j++){
				f.positions[j] = o.positions[j];
				f.normals[j] = o.positions[j];
			}
			m.faces.set(i, f);
		}
		return m;
	}

	//given an OBJMesh, generates its vertex normals
	public static ArrayList<Vector3> genNormals (OBJMesh m){

		ArrayList<Vector3> vertNormals = new ArrayList<Vector3>();
		for(int i = 0; i < m.positions.size();i++){
			vertNormals.add(new Vector3(0,0,0));
		}

		//loop through all the faces, 
		int numT = m.faces.size();
		for (int i = 0; i < numT; i++){ 
			OBJFace f = m.faces.get(i);
			Vector3 p1 = m.getPosition(m.faces.get(i), 0); 
			Vector3 p2 = m.getPosition(m.faces.get(i), 1); 
			Vector3 p3 = m.getPosition(m.faces.get(i), 2); 
			//calculates the normal of the face
			//cross product of two edges gives surface normals
			Vector3 edge1 = p1.clone().add(p2.clone().negate());
			Vector3 edge2 = p1.clone().add(p3.clone().negate());
			Vector3 surfaceNormal = edge1.cross(edge2).normalize();

			//stores the normal into the corresponding vertex
			vertNormals.set(f.positions[0],vertNormals.get(f.positions[0]).add(surfaceNormal));
			vertNormals.set(f.positions[1],vertNormals.get(f.positions[1]).add(surfaceNormal));
			vertNormals.set(f.positions[2],vertNormals.get(f.positions[2]).add(surfaceNormal));
		}

		//loop through all vertices and normalize them.
		for(int i = 0; i < vertNormals.size(); i++){
			vertNormals.set(i, vertNormals.get(i).normalize());
		}

		//the ith term in m.normals should correspond to the ith term in m.position
		return vertNormals;
	}
	//////////////////////////////









	public static void main (String [] args) throws OBJFileFormatException, IOException{
		//process the arguments from the command line
		//inspired by piazza post by Serge-Olivier Amega 
		String outputFile = "default.obj";

		//shape generator mode (problem 1)

		//process user input
		if (args[2].equals("-g")){
			String genMode = "";
			String cylinderDivisor = "32"; String sphereDivisor = "32";//default
			int cDivisor = 32; //default value
			int sDivisor = 32; //default value
			if (args[3].equals("sphere"))
				genMode = "sphere";
			else if (args[3].equals("cylinder"))
				genMode = "cylinder";
			else System.err.println("Error: we can only generate sphere and cylinder");
			for (int i = 4; i < args.length; i++){
				if (i < args.length -1){ //i.e. next argument exists
					String nextArg = args[i+1];
					switch (args[i]){
					case "-n": cylinderDivisor = nextArg; i ++; break;
					case "-m": sphereDivisor = nextArg; i++; break;
					case "-o": outputFile = nextArg; i++; break;
					}
					try{
						cDivisor = Integer.parseInt(cylinderDivisor);
						sDivisor = Integer.parseInt(sphereDivisor);
					}catch (NumberFormatException e){
						System.err.println("Error: must be an int after -n or -m");
					}
				}
			}
			//generate mesh given provided inputs
			if (genMode == "cylinder"){
				OBJMesh m = genCylinder(cDivisor);
				OBJMesh ref = new OBJMesh("cylinder-reference.obj"); //TODO
				try {
					m.writeOBJ(outputFile);
					m.isValid(true);
					//OBJMesh.compare (m,ref, true, (float) 1e-5);	//TODO				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(genMode == "sphere"){

			}
		}
		//normal generator mode (problem 2)
		else if (args[2].equals("-i")){
			String infile = args[3];
			String outfile = args[5];

			OBJMesh ref = new OBJMesh("horse-norms-reference.obj");

			OBJMesh newMesh = normalize(infile);
			newMesh.writeOBJ(outfile);
			//OBJMesh.compare (newMesh,ref, true, (float) 1e-5);	//TODO				

			//currently does nothing
		}else{
			System.out.println("This should not happen");
		}
	}
}
