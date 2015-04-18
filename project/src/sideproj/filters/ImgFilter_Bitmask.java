package sideproj.filters;

import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sideproj.Img;
import sideproj.ImgFilter;

public class ImgFilter_Bitmask extends ImgFilter {

	int bitmask;
	JTextField editor;
	
	public ImgFilter_Bitmask(int bitMask) {
		this.bitmask = bitMask | 0xff000000;
	}
	
	public ImgFilter_Bitmask() {
		this(0xff0000);
	}
	
	@Override
	public void applyTo(Img img) {
		for(int i = 0; i < img.getData().length; i++){
			img.getData()[i] = img.getData()[i] & this.bitmask;
		}	
	}
	

	public static ImgFilter getInstance(int bitmask) {
		return new ImgFilter_Bitmask(bitmask);
	}

	@Override
	public String filterName() {
		return "Bitmask Filter";
	}
	
	@Override
	public Component getEditor() {
		if(editor == null){
			editor = new JTextField(8);
			editor.setText(Integer.toHexString(bitmask));
			editor.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					update(editor.getText());
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					update(editor.getText());
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					update(editor.getText());
				}
				private void update(String text){
					try{
						int number = Integer.parseInt(text, 16);
						bitmask = number | 0xff000000;
					} catch (NumberFormatException e){
					}
				}
			});
		}
		return editor;
	}

	@Override
	public ImgFilter copy() {
		return new ImgFilter_Bitmask(this.bitmask);
	}
	
	
}
