# Webcam Processor

The code builds a webcam painting program in which a portion of the live feed acts as a paintbrush. The program identifies uniform colored regions using a "region growing" algorithm. Region growing initializes a new region at some point that has approximately a specified target color. It then considers the colors of the points neighbors to construct a uniformly colored region which is displayed in the live webcam feed. 
