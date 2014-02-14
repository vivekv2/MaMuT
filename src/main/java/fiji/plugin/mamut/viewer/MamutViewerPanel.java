package fiji.plugin.mamut.viewer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import net.imglib2.realtransform.AffineTransform3D;
import bdv.img.cache.Cache;
import bdv.viewer.SourceAndConverter;
import bdv.viewer.ViewerPanel;
import bdv.viewer.animate.TranslationAnimator;
import fiji.plugin.trackmate.Spot;

public class MamutViewerPanel extends ViewerPanel
{

	private static final long serialVersionUID = 1L;

	/** The overlay on which the {@link TrackMateModel} will be painted. */
	MamutOverlay overlay;

	public MamutViewerPanel( final List< SourceAndConverter< ? >> sources, final int numTimePoints, final Cache cache )
	{
		this( sources, numTimePoints, cache, options() );
	}

	public MamutViewerPanel( final List< SourceAndConverter< ? >> sources, final int numTimePoints, final Cache cache, final Options optional )
	{
		super( sources, numTimePoints, cache, optional );
	}

	@Override
	public void drawOverlays( final Graphics g )
	{
		super.drawOverlays( g );

		if ( null != overlay )
		{
			overlay.setViewerState( state );
			overlay.paint( ( Graphics2D ) g );
		}
	}

	public void centerViewOn( final Spot spot )
	{
		final int tp = spot.getFeature( Spot.FRAME ).intValue();
		setTimepoint( tp );

		final AffineTransform3D t = new AffineTransform3D();
		state.getViewerTransform( t );
		final double[] spotCoords = new double[] { spot.getFeature( Spot.POSITION_X ), spot.getFeature( Spot.POSITION_Y ), spot.getFeature( Spot.POSITION_Z ) };

		// Translate view so that the target spot is in the middle of the
		// JFrame.
		final double dx = display.getWidth() / 2 - ( t.get( 0, 0 ) * spotCoords[ 0 ] + t.get( 0, 1 ) * spotCoords[ 1 ] + t.get( 0, 2 ) * spotCoords[ 2 ] );
		final double dy = display.getHeight() / 2 - ( t.get( 1, 0 ) * spotCoords[ 0 ] + t.get( 1, 1 ) * spotCoords[ 1 ] + t.get( 1, 2 ) * spotCoords[ 2 ] );
		final double dz = -( t.get( 2, 0 ) * spotCoords[ 0 ] + t.get( 2, 1 ) * spotCoords[ 1 ] + t.get( 2, 2 ) * spotCoords[ 2 ] );

		// But use an animator to do this smoothly.
		final double[] target = new double[] { dx, dy, dz };
		currentAnimator = new TranslationAnimator( t, target, 300 );
		currentAnimator.setTime( System.currentTimeMillis() );
		requestRepaint();
	}
}