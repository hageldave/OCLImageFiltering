package sideproj.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class MouseDragAdapter extends MouseAdapter {
	private int xOld, yOld;
	
	public MouseDragAdapter(Component comp) {
		comp.addMouseListener(this);
		comp.addMouseMotionListener(this);
	}
	
	@Override
	public final void mousePressed(MouseEvent e) {
		xOld = e.getXOnScreen();
		yOld = e.getYOnScreen();
	}
	
	@Override
	public final void mouseDragged(MouseEvent e) {
		mouseDragAction(e, new Point(e.getXOnScreen()-xOld, e.getYOnScreen()-yOld));
		xOld = e.getXOnScreen();
		yOld = e.getYOnScreen();
	}
	
	@Override
	public final void mouseReleased(MouseEvent e) {
		mouseReleaseAction(e, new Point(e.getXOnScreen()-xOld, e.getYOnScreen()-yOld));
	}
	
	public void mouseDragAction(MouseEvent e, Point delta){};
	
	public void mouseReleaseAction(MouseEvent e, Point delta){};
}
