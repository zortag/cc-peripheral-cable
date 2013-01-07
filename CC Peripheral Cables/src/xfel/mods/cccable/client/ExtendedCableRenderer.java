package xfel.mods.cccable.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.google.common.util.concurrent.Service.State;

import xfel.mods.cccable.common.blocks.TileCableCommon;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

/**
 * An extend renderer to make cables look nicer (wip)
 * 
 * @author Xfel
 * 
 */
public class ExtendedCableRenderer implements ISimpleBlockRenderingHandler {

	/**
	 * All directions orthogonal to the given directions
	 */
	public static final int[][] ORTHOGONAL_DIRECTIONS = { { 2, 4, 3, 5 },
			{ 2, 4, 3, 5 }, { 1, 5, 0, 4 }, { 1, 4, 0, 5 }, { 1, 2, 0, 3 },
			{ 1, 3, 0, 2 } };

	public static final int[][] OFLAGS = new int[6][4];
	/**
	 * The orthogonal direction masks
	 */
	public static final int[] OMASKS = new int[6];

	static {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				int flag = ForgeDirection.VALID_DIRECTIONS[ORTHOGONAL_DIRECTIONS[i][j]].flag;
				OFLAGS[i][j] = flag;
				OMASKS[i] |= flag;
			}
		}
	}

	/**
	 * Computes the texture index for the given side and connections
	 * 
	 * @param connectionState
	 *            the block's connection state
	 * @param side
	 *            the block side to render
	 * @return the texture index
	 */
	public static int selectTexture(int connectionState, int side) {

		// no connections?
		if (connectionState == 0) {
			return 0;
		}

		connectionState &= OMASKS[side];

		if (connectionState == OMASKS[side]) {// all directions
			return 0;
		}

		int[] orths = OFLAGS[side];
		// boolean[] has = new boolean[4];
		//
		// for (int i = 0; i < 4; i++) {
		// has[i] = (connectionState & ForgeDirection.VALID_DIRECTIONS[i].flag)
		// != 0;
		// }

		for (int i = 0; i < 2; i++) {
			if (connectionState == (orths[i] | orths[i + 2])
					|| connectionState == orths[i]
					|| connectionState == orths[i + 2]) {
				return 16 + i;// straight
			}
		}

		for (int i = 0; i < 4; i++) {
			if (connectionState == (orths[i] | orths[(i + 1) % 4])) {
				return 32 + i; // corner
			}
		}

		for (int i = 0; i < 4; i++) {
			if (connectionState == (orths[i] | orths[(i + 1) % 4] | orths[(i + 2) % 4])) {
				return 48 + i; // T
			}
		}

		// can this even happen?
		System.out.println("Fallthrough: "
				+ Integer.toBinaryString(connectionState));
		return 0;
	}

	/**
	 * Renders a cable with the given connection state and color tag at the
	 * given position.
	 * 
	 * @param renderblocks
	 * @param world
	 * @param block
	 * @param connectionState
	 * @param colorTag
	 * @param x
	 * @param y
	 * @param z
	 */
	private static void renderCable(RenderBlocks renderblocks,
			IBlockAccess world, Block block, int connectionState, int colorTag,
			int x, int y, int z) {
		final float minSize = 0.25f;
		final float maxSize = 0.75f;

		float xmin = (connectionState & ForgeDirection.WEST.flag) != 0 ? 0.0f
				: minSize;
		float ymin = (connectionState & ForgeDirection.DOWN.flag) != 0 ? 0.0f
				: minSize;
		float zmin = (connectionState & ForgeDirection.NORTH.flag) != 0 ? 0.0f
				: minSize;

		float xmax = (connectionState & ForgeDirection.EAST.flag) != 0 ? 1.0f
				: maxSize;
		float ymax = (connectionState & ForgeDirection.UP.flag) != 0 ? 1.0f
				: maxSize;
		float zmax = (connectionState & ForgeDirection.SOUTH.flag) != 0 ? 1.0f
				: maxSize;

		// This code is basically an adapted version of
		// RenderBlocks.renderStandardBlockWithColorMultiplier
		float colorR = 1.0f;
		float colorG = 1.0f;
		float colorB = 1.0f;

		if (EntityRenderer.anaglyphEnable) {
			float colorRtemp = (colorR * 30.0F + colorG * 59.0F + colorB * 11.0F) / 100.0F;
			float colorGtemp = (colorR * 30.0F + colorG * 70.0F) / 100.0F;
			float colorBtemp = (colorR * 30.0F + colorB * 70.0F) / 100.0F;
			colorR = colorRtemp;
			colorG = colorGtemp;
			colorB = colorBtemp;
		}

		Tessellator tess = Tessellator.instance;

		float colorTopFactor = 1.0F;
		float colorBottomFactor = 0.5F;
		float colorNSFactor = 0.8F;
		float colorOWFactor = 0.6F;

		float colorTopR = colorTopFactor * colorR;
		float colorTopG = colorTopFactor * colorG;
		float colorTopB = colorTopFactor * colorB;
		float colorBottomR = colorBottomFactor * colorR;
		float colorBottomG = colorBottomFactor * colorG;
		float colorBottomB = colorBottomFactor * colorB;
		float colorNSR = colorNSFactor * colorR;
		float colorNSG = colorNSFactor * colorG;
		float colorNSB = colorNSFactor * colorB;
		float colorOWR = colorOWFactor * colorR;
		float colorOWG = colorOWFactor * colorG;
		float colorOWB = colorOWFactor * colorB;

		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
		tess.setBrightness(brightness);

		// render Y +- faces
		renderblocks.setCustomBlockBounds(xmin, minSize, zmin, xmax, maxSize,
				zmax);

		tess.setColorOpaque_F(colorBottomR, colorBottomG, colorBottomB);
		renderblocks.renderBottomFace(block, (double) x, (double) y,
				(double) z, selectTexture(connectionState, 0));

		tess.setColorOpaque_F(colorTopR, colorTopG, colorTopB);
		renderblocks.renderTopFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 1));

		// render Z +- faces
		renderblocks.setCustomBlockBounds(xmin, ymin, minSize, xmax, ymax,
				maxSize);

		tess.setColorOpaque_F(colorNSR, colorNSG, colorNSB);
		renderblocks.renderEastFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 2));

		renderblocks.renderWestFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 3));

		// render X +- faces
		renderblocks.setCustomBlockBounds(minSize, ymin, zmin, maxSize, ymax,
				zmax);

		tess.setColorOpaque_F(colorOWR, colorOWG, colorOWB);
		renderblocks.renderNorthFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 4));

		renderblocks.renderSouthFace(block, (double) x, (double) y, (double) z,
				selectTexture(connectionState, 5));

		if (colorTag != -1) {
			final float colorOffset = 0.001f;

			renderblocks.setCustomBlockBounds(minSize - colorOffset, minSize
					- colorOffset, minSize - colorOffset,
					maxSize + colorOffset, maxSize + colorOffset, maxSize
							+ colorOffset);
			renderblocks.overrideBlockTexture = 64 + colorTag;

			renderblocks.renderStandardBlockWithColorMultiplier(block, x, y, z,
					colorR, colorG, colorB);

			renderblocks.overrideBlockTexture = -1;
		}
		renderblocks.resetCustomBlockBounds();

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, 10);
		Tessellator tessellator = Tessellator.instance;

		int textureID = 0;

		renderer.setCustomBlockBounds(0.25f, 0.0F, 0.25f, 0.75f, 1.0F, 0.75f);

		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderer.renderBottomFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderTopFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderer.renderEastFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderWestFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderNorthFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderSouthFace(block, 0.0D, 0.0D, 0.0D, textureID);
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderer.resetCustomBlockBounds();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileCableCommon) {
			TileCableCommon pipeTile = (TileCableCommon) tile;
			renderCable(renderer, world, block, pipeTile.getConnectionState(),
					pipeTile.getColorTag(), x, y, z);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return 0;
	}
}
