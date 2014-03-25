package de.gmx.worldsbegin.mhmu.client.model.exObj;

import static java.lang.String.format;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_CURRENT_PROGRAM;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL41.GL_FRAGMENT_SHADER_BIT;
import static org.lwjgl.opengl.GL41.GL_GEOMETRY_SHADER_BIT;
import static org.lwjgl.opengl.GL41.GL_TESS_CONTROL_SHADER_BIT;
import static org.lwjgl.opengl.GL41.GL_TESS_EVALUATION_SHADER_BIT;
import static org.lwjgl.opengl.GL41.glBindProgramPipeline;
import static org.lwjgl.opengl.GL41.glCreateShaderProgram;
import static org.lwjgl.opengl.GL41.glGenProgramPipelines;
import static org.lwjgl.opengl.GL41.glUseProgramStages;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER_BIT;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.MobsterHunterMinedomUnite;

/**
 * 
 * @author Carbon
 * 
 * @version 0.1.1a_07.11.2013
 */
@SideOnly(Side.CLIENT)
public class MHModelObject implements IModelCustom {
	protected class SubGroup { // Kind of like a struct
		public int indicesBuff;
		public int faceCount;
		public SubGroup(int indicesBuff, int faceC) {
			this.indicesBuff = indicesBuff;
			this.faceCount = faceC;
		}
	}

	protected static final int GL_BUT_VERTEX_SHADER = // GL_VERTEX_SHADER_BIT |
	GL_TESS_CONTROL_SHADER_BIT | GL_TESS_EVALUATION_SHADER_BIT
			| GL_GEOMETRY_SHADER_BIT | GL_FRAGMENT_SHADER_BIT
			| GL_COMPUTE_SHADER_BIT;

	/** For {@link #getStatus()} */
	public static final int NOT_LOADED = -0x1;
	public static final int SUCCESS = 0x0;
	public static final int FAIL_INPUTSTREAM = 0x1;
	public static final int FAIL_FORMAT = 0x2;
	public static final int FAIL_INVALID_LOCATION = 0x3;

	public static final int VTX_XYZ_L = 3; // Length in bytes
	public static final int VTX_XYZW_L = 4;
	public static final int UV_L = 2;
	public static final int NORM_L = 3;
	public static final int VTX_ALL_L = VTX_XYZ_L + UV_L + NORM_L;
	public static final int VTX_ALLW_L = VTX_XYZW_L + UV_L + NORM_L;
	public static final int BONE_L = 12;
	public static final int HEADER_L = 32;

	public static final short magicNbr = (short) 0xe69d;

	public static final int MAX_BONE_BINDINGS = 4;
	public static final int HEADER_OVERHEAD = 16;

	protected static int geometryProgramArm;
	protected static int geometryProgramNoArm;
	protected static int pipeline;

	static {
		String program = //
		"layout (location = 0) in vec4 vtx_pos;" //
				+ "layout (location = 2) in vec3 vtx_norm;" //
				+ "layout (location = 2) out vec3 normal;" // is this correct?
				+ "void main (void)" //
				+ "{" //
				+ "  gl_Position = vtx_pos;" //
				+ "  normal      = vtx_norm;" //
				+ "}"; //
		pipeline = glGenProgramPipelines();
		geometryProgramArm = glCreateShaderProgram(GL_VERTEX_SHADER, program);
		geometryProgramNoArm = 0; // TODO create shader for no armature
		glUseProgram(0);
	}

	protected static final DoubleBuffer dirDoubleBuff(double[] arrayFrom) {
		return ByteBuffer.allocateDirect(arrayFrom.length * 4)
				.order(ByteOrder.nativeOrder()).asDoubleBuffer().put(arrayFrom);
	}

	protected static final FloatBuffer dirFloatBuff(float[] arrayFrom) {
		return ByteBuffer.allocateDirect(arrayFrom.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer().put(arrayFrom);
	}

	protected static final IntBuffer dirIntBuff(int[] arrayFrom) {
		return ByteBuffer.allocateDirect(arrayFrom.length * 4)
				.order(ByteOrder.nativeOrder()).asIntBuffer().put(arrayFrom);
	}

	/**
	 * @throws EOFException
	 *             , IOException
	 */
	protected static final int getBinding(DataInputStream dis,
			float[] valueArray, int[] indexArray, int boneCount, int offset)
			throws EOFException, IOException {
		for (int i = 0; i < MAX_BONE_BINDINGS; ++i) {
			valueArray[offset + i] = 0.0F;
			indexArray[offset + i] = 0;
		}

		int toRead = maxBindings(boneCount);
		int i = 0;
		for (; i < toRead; ++i) {
			// values
			valueArray[offset + i] = dis.readFloat();
			// indices
			indexArray[offset + i] = dis.readByte() & 0xFF;
		}
		return i;
	}

	/**
	 * @param is
	 * @return number of read bytes
	 * @throws EOFException
	 *             , IOException
	 */
	protected static final int getBone(DataInputStream is, double[] buffer,
			int offset) throws EOFException, IOException {
		int i = 0;
		// read matrix and inverse matrix
		for (; i < BONE_L * 2; ++i) {
			buffer[offset + i] = is.readDouble();
		}
		return i * 4;
	}
	/**
	 * Reads a normal from the input stream
	 * 
	 * @param is
	 * @return number of read bytes
	 * @throws IOException
	 */
	protected static final int getNormal(DataInputStream is, float[] buffer,
			int offset) throws EOFException, IOException {
		int i = 0;
		for (; i < NORM_L; ++i) {
			buffer[offset + i] = is.readFloat();
		}
		return i * 4;
	}

	/**
	 * @param is
	 * @return number of read bytes
	 * @throws EOFException
	 *             , IOException
	 */
	protected static final void getUniformMat(double[] bones2, int offset)
			throws EOFException, IOException {
		// fill with uniform matrix
		for (int i = 0; i < BONE_L * 2; ++i) {
			bones2[offset + i] = (((i % 12) % 5) == 0 ? 1 : 0);
		}
	}

	protected static final byte[] getUTF8bytes(DataInputStream dis)
			throws IOException {
		byte curr;
		byte[] arr = new byte[0];
		// 0xff is not valid utf-8 byte and our ending signal
		while ((curr = dis.readByte()) != 0xff) {
			byte[] tmp = arr;
			arr = new byte[tmp.length + 1];
			for (int i = 0; i < tmp.length; ++i) {
				arr[i] = tmp[i];
			}
			arr[tmp.length] = curr;
		}

		return arr;
	}
	/**
	 * Reads an uv-mappping from the stream
	 * 
	 * @param is
	 * @return number of read bytes
	 * @throws IOException
	 */
	protected static final int getUVMapping(DataInputStream is, float[] buffer,
			int offset) throws EOFException, IOException {
		int i = 0;
		for (; i < UV_L; ++i) {
			buffer[offset + i] = is.readFloat();
		}
		return i * 4;
	}
	/**
	 * Reads a new vertex from the input stream and puts it in the list.
	 * 
	 * @param is
	 * @return number of read bytes
	 * @throws IOException
	 */
	protected static final int getVertex(DataInputStream is, float[] buffer,
			int offset) throws EOFException, IOException {
		int i = 0;
		for (; i < VTX_XYZ_L; ++i) {
			buffer[offset + i] = is.readFloat();
		}
		buffer[offset + 3] = 1.0F; // add w-coord
		return i;
	}

	protected static final int maxBindings(int boneCount) {
		return Math.min(0, Math.min(boneCount, MAX_BONE_BINDINGS));
	}

	private static final int readBoneCount(DataInputStream dis)
			throws ModelFormatException, IOException {
		try {
			return dis.readByte() & 0xFF;
		} catch (EOFException e) {
			throw new ModelFormatException(
					"Unsuspected end of stream. Could not read bone count.", e);
		}
	}

	private static final int readFaceCount(DataInputStream dis)
			throws ModelFormatException, IOException {
		try {
			int faceCount = dis.readInt();
			if (faceCount < 0)
				throw new ModelFormatException(
						"Weird (negative) draw-index count: " + faceCount);
			return faceCount;
		} catch (EOFException e) {
			throw new ModelFormatException(
					"Unsuspected end of stream. Could not read face count.", e);
		}
	}
	private static final void readHeaderOverhead(DataInputStream dis)
			throws ModelFormatException, IOException {
		try {
			for (int i = 0; i < HEADER_OVERHEAD; ++i) { // 16 buffer bytes
				dis.readByte();
			}
		} catch (EOFException e) {
			throw new ModelFormatException(
					"Unsuspected end of stream. Expected at least a full header.",
					e);
		}
	}
	private static final int readSubGCount(DataInputStream dis)
			throws ModelFormatException, IOException {
		try {
			return dis.readByte() & 0xFF;
		} catch (EOFException e) {
			throw new ModelFormatException(
					"Unsuspected end of stream. Could not read bone count.", e);
		}
	}

	private static final int readVersion(DataInputStream dis)
			throws ModelFormatException, IOException {
		try {
			return dis.readInt();
		} catch (EOFException e) {
			throw new ModelFormatException(
					"Unsuspected end of stream. Could not read version of file.",
					e);
		}
	}
	private static final int readVtxCount(DataInputStream dis)
			throws ModelFormatException, IOException {
		try {
			int vtxCount = dis.readInt();
			if (vtxCount < 0)
				throw new ModelFormatException(
						"Weird (negative) vertex count: " + vtxCount);
			return vtxCount;
		} catch (EOFException e) {
			throw new ModelFormatException(
					"Unsuspected end of stream. Could not read vertex count.",
					e);
		}
	}
	private IResourceManager resManager;
	private ResourceLocation objLoc;
	/** If the model fails to load. Prevents rendering. */
	private int status;
	protected int faceCount; // In order to determine the number of elements to

	// render
	/** The one buffer where pos, uv and norms are in */
	protected int vtxNormUvsBuff; // values are in vertexorder and displaced
	protected int indicesBuff; // Ready for openGL to process

	protected int bindingValueBuff;

	protected int bindingIndexBuff;

	protected int boneBuff; // double
	protected Map<String, SubGroup> nameToSubGroup;

	// TODO protected float[] currentTransform; for animations etc
	// IDEA create a static get(ResourceLocation method) to prevent reading it
	// again...
	// duplicating an object isn't that complicated... just copy
	// resManager, objLoc, status, vtxNormUvsBuff, faceCount, indicesBuff, very
	// lightweight, can also just hand over the map, no need to dupe
	public MHModelObject(IResourceManager resManager, ResourceLocation objLoc) {
		this.resManager = resManager;
		this.objLoc = objLoc;
		this.status = NOT_LOADED;
		this.nameToSubGroup = new HashMap<String, SubGroup>();
	}

	protected int bindPipeline() {
		int currProgram = glGetInteger(GL_CURRENT_PROGRAM);
		if (currProgram != 0) {
			glUseProgramStages(pipeline, GL_BUT_VERTEX_SHADER, currProgram);
			glUseProgramStages(pipeline, GL_VERTEX_SHADER, this.boneBuff == 0
					? geometryProgramNoArm
					: geometryProgramArm);
			glUseProgram(0);
			glBindProgramPipeline(pipeline);
		} else {
			glUseProgram(geometryProgramArm);
		}
		return currProgram;
	}

	protected void bindStaticBuffers() {
		glEnableClientState(GL_VERTEX_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, this.vtxNormUvsBuff);
		// __POS
		glEnableVertexAttribArray(0); // We like submitting vertices on
										// stream 0
		glVertexAttribPointer(0, // manipulate stream 0
				VTX_ALLW_L, // we have 4 coordinates
				GL_FLOAT, false, // not normalized
				VTX_ALLW_L, // length of every element is size of all together
				0); // offset is 0
		// __UV
		glEnableVertexAttribArray(8); // We like submitting uvs on
										// stream 8 which is default for
										// openGL-texcoords
		glVertexAttribPointer(8, // manipulate stream 8
				UV_L, // we have 2 coordinates
				GL_FLOAT, false, // not normalized
				VTX_ALLW_L, // length of every element is size of all together
				VTX_XYZW_L * 4); // offset is 16 bytes
		// // __NORMS
		glEnableVertexAttribArray(1); // We like submitting normals on
										// stream 1
		glVertexAttribPointer(1, // manipulate stream 1
				NORM_L, // we have 3 coordinates
				GL_FLOAT, false, // not normalized
				VTX_ALLW_L, // length of every element is size of all together
				(VTX_XYZW_L + UV_L) * 4); // offset is 24

		// is this supposed to be done here? or can we bind them once?
		// int loc = GL20.glGetUniformLocation(geometryProgramArm,
		// "boneMatrices");
		// TODO GL41.glProgramUniformMatrix4x3(geometryProgramArm, loc,
		// GL11.GL_FALSE, bonesBuff);
		// int locInv = GL20.glGetUniformLocation(geometryProgramArm,
		// "boneMatricesInv");
		// GL41.glProgramUniformMatrix4x3(geometryProgramArm, locInv,
		// GL11.GL_FALSE, bonesBuffInv);
	}

	public int getStatus() {
		return this.status;
	}

	@Override
	public String getType() {
		return "CustomModelFormat";
	}

	protected final void loadFromRes() {
		if (this.status == NOT_LOADED) {
			if (this.objLoc != null) {
				if (this.resManager == null) {
					this.resManager = Minecraft.getMinecraft()
							.getResourceManager();
				}

				try (InputStream objResStream = this.resManager.getResource(
						this.objLoc).getInputStream()) {
					this.loadModel(new DataInputStream(new BufferedInputStream(
							objResStream)));
				} catch (ModelFormatException mfe) {
					// Wrong file format
					Logger log = MobsterHunterMinedomUnite.instance()
							.getLogger();
					log.log(Level.WARN,
							format("The file %s is not a valid "
									+ "Mobster Hunter Model file. Please refer to "
									+ "PLACEHOLDER" // TODO tutorial
													// address YT?
									+ " in order to get your things straight.",
									this.objLoc));
					this.status = FAIL_FORMAT;
				} catch (FileNotFoundException fnfe) {
					// File not found, fails
					this.status = FAIL_INVALID_LOCATION;
					Logger log = MobsterHunterMinedomUnite.instance()
							.getLogger();
					log.log(Level.WARN, format("The file %s could not be found"
							+ "when trying to read a model file.", this.objLoc));
				} catch (IOException ioe) {
					// InputStream fails
					this.status = FAIL_INPUTSTREAM;
					Logger log = MobsterHunterMinedomUnite.instance()
							.getLogger();
					log.log(Level.WARN, format("The InputStream for file %s "
							+ "threw an IOException: %s", ioe));
				}
			} else {
				// File is null
				this.status = FAIL_INVALID_LOCATION;
				Logger log = MobsterHunterMinedomUnite.instance().getLogger();
				log.log(Level.WARN,
						format("The object location given was null."
								+ "Constructor doesn't throw to maintain continuum."));
			}
			if (this.status == NOT_LOADED) {
				// No errorbit set
				this.status = SUCCESS;
			}
		}
	}

	// TODO rewrite this to read from an mhdscr-file not the model-file itself
	protected final void loadModel(DataInputStream dis)
			throws ModelFormatException, IOException {
		Logger log = MobsterHunterMinedomUnite.instance().getLogger();
		glEnableClientState(GL_VERTEX_ARRAY);
		try {
			// Header
			// s(short)+s(int)+s(int)+s(int)+s(byte)+s(byte) + 16 freebytes =
			// 1*2+3*4+2*1+16 = 32 bytes -> update HEADER_L
			// magicNbr
			if (dis.readShort() != magicNbr)
				throw new ModelFormatException(
						"Not a legal mhModelObjectFile. Incorrect magic number.");
			// version
			@SuppressWarnings("unused")
			int version = readVersion(dis);
			// vtxCount
			int vtxCount = readVtxCount(dis);
			// faceCount
			int faceCount = readFaceCount(dis);
			// boneCount
			// Has an effective range of [0...255]
			int boneCount = readBoneCount(dis);
			// subGCount
			// Has an effective range of [0...255]
			int subGCount = readSubGCount(dis);
			readHeaderOverhead(dis);

			// INITIALIZATION
			float[] allVtxs = new float[vtxCount * VTX_ALLW_L];
			int[] indices = new int[faceCount * 3];

			// Body
			int bytePointInFile = HEADER_L;

			// verts VTX_XYZ_L * vtxCount * s(int) =
			// 3*c*4 = 12*c
			// position
			int i = 0;
			try {
				for (i = 0; i < vtxCount; ++i) {
					int readBytes = MHModelObject.getVertex(dis, allVtxs, i
							* VTX_ALLW_L);
					bytePointInFile += readBytes;
				}
			} catch (EOFException e) {
				throw new ModelFormatException(
						"Unsuspected end of file around point "
								+ bytePointInFile
								+ " during reading vertices (nbr." + i
								+ " out of " + vtxCount + ").", e);
			}
			// uvs
			try {
				for (i = 0; i < vtxCount; ++i) {
					int readBytes = MHModelObject.getUVMapping(dis, allVtxs, i
							* VTX_ALLW_L + VTX_XYZW_L);
					bytePointInFile += readBytes;
				}
			} catch (EOFException e) {
				throw new ModelFormatException(
						"Unsuspected end of file around point "
								+ bytePointInFile
								+ " during reading uvMappings (nbr." + i
								+ " out of " + vtxCount + ").", e);
			}
			// normals
			try {
				for (i = 0; i < vtxCount; ++i) {
					int readBytes = MHModelObject.getNormal(dis, allVtxs, i
							* VTX_ALLW_L + VTX_XYZW_L + UV_L);
					bytePointInFile += readBytes;
				}
			} catch (EOFException e) {
				throw new ModelFormatException(
						"Unsuspected end of file around point "
								+ bytePointInFile
								+ " during reading normals (nbr." + i
								+ " out of " + vtxCount + ").", e);
			}
			// buffer that all
			int vtxNormUvsBuff = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vtxNormUvsBuff);
			glBufferData(GL_ARRAY_BUFFER, dirFloatBuff(allVtxs), GL_STATIC_DRAW);
			// index-order
			int indicesBuff = 0;
			try {
				for (i = 0; i < faceCount * 3; ++i) {
					indices[i] = dis.readInt();
					if (indices[i] < 0 || indices[i] >= vtxCount)
						throw new ModelFormatException(
								"Can't refer to a not existing vertex, at byte "
										+ bytePointInFile + ").");
					bytePointInFile += 4;
				}

				indicesBuff = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER, indicesBuff);
				glBufferData(GL_ARRAY_BUFFER, dirIntBuff(indices),
						GL_STATIC_DRAW);
			} catch (EOFException e) {
				throw new ModelFormatException(
						"Unsuspected end of file around point "
								+ bytePointInFile
								+ " during reading the vtxList for rendering (nbr."
								+ i + " out of " + faceCount * 3 + ").", e);
			}
			log.log(Level.DEBUG,
					format("The vertex descriptions for modelfile %s"
							+ "have been read."));
			// bones
			int boneAndInvBuff = 0;
			try {
				if (boneCount != 0) {
					double[] bones = new double[256 * BONE_L * 2];
					for (i = 0; i < boneCount; ++i) {
						int readBytes = getBone(dis, bones, i * BONE_L * 2);
						int readBytesInv = getBone(dis, bones, i * BONE_L * 2
								+ BONE_L);
						bytePointInFile += (readBytes + readBytesInv);
					}
					for (; i < 256; ++i) {
						getUniformMat(bones, i * BONE_L * 2);
					}
					boneAndInvBuff = glGenBuffers();
					glBindBuffer(GL_UNIFORM_BUFFER, boneAndInvBuff);
					glBufferData(GL_UNIFORM_BUFFER, dirDoubleBuff(bones),
							GL_STATIC_DRAW);
				}
			} catch (EOFException e) {
				throw new ModelFormatException(
						"Unsuspected end of file around point "
								+ bytePointInFile
								+ " while reading a bone-matrix", e);
			}
			// bindings
			int bindingValueBuff = 0;
			int bindingIndexBuff = 0;
			try {
				float[] bindingsValues = new float[vtxCount * MAX_BONE_BINDINGS];
				int[] bindingIndices = new int[vtxCount * MAX_BONE_BINDINGS];
				for (i = 0; i < vtxCount; ++i) {
					int readBytes = getBinding(dis, bindingsValues,
							bindingIndices, boneCount, i * MAX_BONE_BINDINGS);
					bytePointInFile += readBytes;
				}
				bindingValueBuff = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER, bindingValueBuff);
				glBufferData(GL_ARRAY_BUFFER, dirFloatBuff(bindingsValues),
						GL_STATIC_DRAW);
				bindingIndexBuff = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER, bindingIndexBuff);
				glBufferData(GL_ARRAY_BUFFER, dirIntBuff(bindingIndices),
						GL_STATIC_DRAW);
			} catch (EOFException e) {
				throw new ModelFormatException(
						"Unsuspected end of file around point "
								+ bytePointInFile
								+ " while reading the bindings for vertex nbr. "
								+ i + ".", e);
			}

			log.log(Level.DEBUG, "The bones for the modelfile %s"
					+ "have been read. bone count: %d", this.objLoc, boneCount);
			// Animations are present in the description file

			// Subgroups
			CharsetDecoder cs = Charset.forName("UTF-8").newDecoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT);
			Map<String, SubGroup> nameToSubGroup = new HashMap<String, SubGroup>();
			for (i = 0; i < subGCount; ++i) {
				String name = "";
				int indicesBuffSubG;
				int faceCountSubG = 0;

				// name of subgroup
				try {
					byte[] nameBytes = getUTF8bytes(dis);
					name = cs.decode(ByteBuffer.wrap(nameBytes)).toString();
					bytePointInFile += nameBytes.length;
					if (name.isEmpty())
						throw new ModelFormatException(
								"The name of a subgroup cannot be empty.");
					if (nameToSubGroup.containsKey(name))
						throw new ModelFormatException(String.format(
								"A subgroup with the name %s already exists.",
								name));
				} catch (EOFException e) {
					throw new ModelFormatException(
							"Unexpected end of file at byte " + bytePointInFile
									+ " whilst reading the name of a subgroup.",
							e);
				}
				// facecount for subGroup
				try {
					faceCountSubG = dis.readInt();
					bytePointInFile += 4;
					if (faceCountSubG < 0)
						throw new ModelFormatException(
								"Face count for subgroup " + name
										+ " can not be less than zero.");
				} catch (EOFException e) {
					throw new ModelFormatException(
							"Unexpected end of file at byte " + bytePointInFile
									+ ", reading faceCount for subgroup "
									+ name + ".", e);
				}
				// read indices
				try {
					int[] subGindeces = new int[faceCountSubG * 3];
					for (int j = 0; j < subGindeces.length; ++j) {
						int index = dis.readInt();
						bytePointInFile += 4;
						if (index >= vtxCount)
							throw new ModelFormatException(
									"Can't refer to a not existing vertex, at byte "
											+ bytePointInFile + ", subGroup "
											+ name + ".");
						subGindeces[j] = index;
					}

					indicesBuffSubG = glGenBuffers();
					glBufferData(GL_ARRAY_BUFFER, dirIntBuff(subGindeces),
							GL_STATIC_DRAW);

				} catch (EOFException e) {
					throw new ModelFormatException(
							"Unexpected end of file, could not read next index for a face, at byte "
									+ bytePointInFile + ", subGroup " + name
									+ ".", e);
				}
				// Place the subgroup into out map
				nameToSubGroup.put(name, new SubGroup(indicesBuffSubG,
						faceCountSubG));

				log.log(Level.DEBUG, "A subgroup for the modelfile %s"
						+ "has been read. Name of subgroup: %s, "
						+ "facecount for subgroup : %d", name, faceCountSubG);
			} // end of subgroups

			// TODO read subgroup-linkage

			this.faceCount = faceCount;
			this.vtxNormUvsBuff = vtxNormUvsBuff;
			this.indicesBuff = indicesBuff;
			this.bindingValueBuff = bindingValueBuff;
			this.bindingIndexBuff = bindingIndexBuff;
			// this.boneBuff = boneBuff;
			this.nameToSubGroup = nameToSubGroup;

			log.log(Level.INFO, "The file %s has been loaded "
					+ "correctly and is ready to be used as a model.",
					this.objLoc);
		} finally {
			glDisableClientState(GL_VERTEX_ARRAY);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
	}

	/**
	 * 
	 * @param resManager
	 *            the resource-manager to reload this model with
	 */
	public void reloadModel(IResourceManager resManager) {
		if (resManager != this.resManager) {
			this.resManager = resManager;
			this.status = NOT_LOADED;

			this.faceCount = 0;

			this.vtxNormUvsBuff = 0;
			this.indicesBuff = 0;
			this.bindingValueBuff = 0;
			this.bindingIndexBuff = 0;
			this.boneBuff = 0;

			this.nameToSubGroup.clear();

			this.loadFromRes();
		}
	}
	@Override
	public void renderAll() {
		this.loadFromRes();
		if (this.shouldRender()) {
			System.out.println("Render got called!!!");
			this.bindStaticBuffers();
			int prevProgram = this.bindPipeline();

			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indicesBuff);
			glDrawElements(GL_TRIANGLES, this.faceCount * 3, // Triangles
					GL_UNSIGNED_INT, 0); // No offsets

			this.unbindPipeline(prevProgram);
			this.unbindStaticBuffers();
		}
	}
	@Override
	public void renderAllExcept(String... excludedGroupNames) {
		this.loadFromRes();
		if (this.shouldRender()) {
			this.bindStaticBuffers();
			int prevProgram = this.bindPipeline();

			subGLoop : for (String subGName : this.nameToSubGroup.keySet()) {
				for (String excluded : excludedGroupNames) {
					if (subGName.equals(excluded)) {
						continue subGLoop;
					}
				}

				// Null check not necessary
				SubGroup subG = this.nameToSubGroup.get(subGName);
				glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, subG.indicesBuff);
				glDrawElements(GL_TRIANGLES, subG.faceCount * 3, // Triangles
						GL_UNSIGNED_INT, 0); // No offsets
			}

			this.unbindPipeline(prevProgram);
			this.unbindStaticBuffers();
		}
	}

	@Override
	public void renderOnly(String... groupNames) {
		this.loadFromRes();
		if (this.shouldRender()) {
			this.bindStaticBuffers();
			int prevProgram = this.bindPipeline();

			for (String partName : groupNames) {
				SubGroup subG;
				if ((subG = this.nameToSubGroup.get(partName)) != null) {
					glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, subG.indicesBuff);
					glDrawElements(GL_TRIANGLES, subG.faceCount * 3, // Triangles
							GL_UNSIGNED_INT, 0); // No offsets
				}
			}

			this.unbindPipeline(prevProgram);
			this.unbindStaticBuffers();
		}
	}

	@Override
	public void renderPart(String partName) {
		this.loadFromRes();
		if (this.shouldRender()) {
			this.bindStaticBuffers();
			int prevProgram = this.bindPipeline();

			SubGroup subG;
			if ((subG = this.nameToSubGroup.get(partName)) != null) {
				glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, subG.indicesBuff);
				glDrawElements(GL_TRIANGLES, subG.faceCount * 3, // Triangles
						GL_UNSIGNED_INT, 0); // No offsets
			}

			this.unbindPipeline(prevProgram);
			this.unbindStaticBuffers();
		}
	}

	protected boolean shouldRender() {
		return this.status == SUCCESS;
	}

	protected void unbindPipeline(int prevProgram) {
		glUseProgramStages(pipeline, GL_BUT_VERTEX_SHADER, 0);
		glBindProgramPipeline(0);
		glUseProgram(prevProgram);
	}

	protected void unbindStaticBuffers() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(8);
		glDisableClientState(GL_VERTEX_ARRAY);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}