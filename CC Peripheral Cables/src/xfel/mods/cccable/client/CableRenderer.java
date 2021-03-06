/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.cccable.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import xfel.mods.cccable.common.blocks.TileCableCommon;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

/**
 * Renderer for the peripheral cable.
 * 
 * @author Xfel
 *
 */
@Deprecated
public class CableRenderer implements ISimpleBlockRenderingHandler {

	private static void renderCable(RenderBlocks renderblocks,
			IBlockAccess world, Block block, int connectionState, int x, int y,
			int z) {
		float minSize = 0.25f;
		float maxSize = 0.75f;

		// state.currentTextureIndex =
		// state.textureMatrix.getTextureIndex(ForgeDirection.UNKNOWN);
		renderblocks.setCustomBlockBounds(minSize, minSize, minSize, maxSize, maxSize,
				maxSize);
		renderblocks.renderStandardBlock(block, x, y, z);

		if ((connectionState & ForgeDirection.WEST.flag) != 0) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.WEST);
			renderblocks.setCustomBlockBounds(0.0F, minSize, minSize, minSize, maxSize,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if ((connectionState & ForgeDirection.EAST.flag) != 0) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.EAST);
			renderblocks.setCustomBlockBounds(maxSize, minSize, minSize, 1.0F, maxSize,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if ((connectionState & ForgeDirection.DOWN.flag) != 0) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.DOWN);
//			block.setBlockBounds(minSize, 0.0F, minSize, maxSize, minSize,
//					maxSize);
			renderblocks.setCustomBlockBounds(minSize, 0.0F, minSize, maxSize, minSize,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if ((connectionState & ForgeDirection.UP.flag) != 0) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.UP);
			renderblocks.setCustomBlockBounds(minSize, maxSize, minSize, maxSize, 1.0F,
					maxSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if ((connectionState & ForgeDirection.NORTH.flag) != 0) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.NORTH);
			renderblocks.setCustomBlockBounds(minSize, minSize, 0.0F, maxSize, maxSize,
					minSize);
			renderblocks.renderStandardBlock(block, x, y, z);
		}

		if ((connectionState & ForgeDirection.SOUTH.flag) != 0) {
			// state.currentTextureIndex =
			// state.textureMatrix.getTextureIndex(ForgeDirection.SOUTH);
			renderblocks.setCustomBlockBounds(minSize, minSize, maxSize, maxSize, maxSize,
					1.0F);
			renderblocks.renderStandardBlock(block, x, y, z);
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
					x, y, z);
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
