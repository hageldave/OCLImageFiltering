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
	public void mousePressed(MouseEvent e) {
		xOld = e.getXOnScreen();
		yOld = e.getYOnScreen();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseDragAction(e, new Point(e.getXOnScreen()-xOld, e.getYOnScreen()-yOld));
		xOld = e.getXOnScreen();
		yOld = e.getYOnScreen();
	}
	
	public abstract void mouseDragAction(MouseEvent e, Point delta);
}
