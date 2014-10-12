package com.sighthunt.algorithm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sighthunt.util.ImageFiles;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageMatcher {

	private int mPrepro;
	FeatureDetector mFeatureDetector;
	DescriptorExtractor mDescriptorExtractor;
	DescriptorMatcher mMatcher;
	float mThreshold;

	MatOfKeyPoint mKeyPoints1;

	public MatOfKeyPoint getKeyPoints1() {
		return mKeyPoints1;
	}

	MatOfKeyPoint mKeyPoints2;

	public MatOfKeyPoint getKeyPoints2() {
		return mKeyPoints2;
	}

	List<DMatch> mMatches;

	public List<DMatch> getMatches() {
		return mMatches;
	}

	float mHeight;

	public float getHeight() {
		return mHeight;
	}

	float mWidth;

	public float getWidth() {
		return mWidth;
	}


	public ImageMatcher(int featureDetector, int descriptorExtractor, int descriptorMatcher, float threshold, int prepro) {
		mFeatureDetector = FeatureDetector.create(featureDetector);
		mDescriptorExtractor = DescriptorExtractor.create(descriptorExtractor);
		mMatcher = DescriptorMatcher.create(descriptorMatcher);
		mThreshold = threshold;
		mPrepro = prepro;
	}

	public float getImageMatchingScore() {

		Mat image1 = null;
		Mat image2 = null;

		switch (mPrepro) {
			case 0: {
				image1 = getMatFromFile(ImageFiles.ORIGINAL_IMAGE);
				image2 = getMatFromFile(ImageFiles.MATCH_IMAGE);
				break;
			}
			case 1:

				image1 = binaryFile(ImageFiles.ORIGINAL_IMAGE, ImageFiles.ORIGINAL_IMAGE_PREPRO);
				image2 = binaryFile(ImageFiles.MATCH_IMAGE, ImageFiles.MATCH_IMAGE_PREPRO);
				break;
			case 2: {

				image1 = edgeDetectionFile(ImageFiles.ORIGINAL_IMAGE, ImageFiles.ORIGINAL_IMAGE_PREPRO);
				image2 = edgeDetectionFile(ImageFiles.MATCH_IMAGE, ImageFiles.MATCH_IMAGE_PREPRO);
				break;
			}
			default: {
			}

		}

		mHeight = (float) image1.size().height;
		mWidth = (float) image1.size().width;

		mKeyPoints1 = getKeypoints(image1);
		mKeyPoints2 = getKeypoints(image2);

		Mat descriptor1 = getDescriptors(image1, mKeyPoints1);
		Mat descriptor2 = getDescriptors(image2, mKeyPoints2);

		MatOfDMatch matches = new MatOfDMatch();

		mMatcher.match(descriptor1, descriptor2, matches);

		DMatch[] matchesArray = matches.toArray();

		mMatches = new ArrayList<DMatch>();
		for (int i = 0; i < descriptor1.rows(); i++) {
			if (matchesArray[i].distance <= mThreshold) {
				mMatches.add(matchesArray[i]);
			}
		}

		return 0;
	}

	private Mat binaryFile(File in, File out) {

		Mat imgSource = Highgui.imread(in.getAbsolutePath());
		Imgproc.threshold(imgSource, imgSource, -1, 255,
				Imgproc.THRESH_BINARY_INV);

		Highgui.imwrite(out.getAbsolutePath(), imgSource);
		return imgSource;
	}

	private Mat edgeDetectionFile(File in, File out) {

		Mat imgSource = Highgui.imread(in.getAbsolutePath());
		Imgproc.Canny(imgSource, imgSource, 500, 1000, 5, true);

		Highgui.imwrite(out.getAbsolutePath(), imgSource);
		return imgSource;
	}

	private Mat getMatFromFile(File imageFile) {
		Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
		Mat image = new Mat();
		Utils.bitmapToMat(bitmap, image);
		return image;
	}

	private Mat getDescriptors(Mat image, MatOfKeyPoint keyPoints) {
		Mat descriptors = new Mat();
		mDescriptorExtractor.compute(image, keyPoints, descriptors);
		return descriptors;
	}

	private MatOfKeyPoint getKeypoints(Mat image) {

		MatOfKeyPoint keyPoints = new MatOfKeyPoint();

		mFeatureDetector.detect(image, keyPoints);

		return keyPoints;
	}
}
